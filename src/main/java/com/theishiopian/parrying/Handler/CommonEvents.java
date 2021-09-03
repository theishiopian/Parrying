package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.APItem;
import com.theishiopian.parrying.Items.FlailItem;
import com.theishiopian.parrying.Mechanics.*;
import com.theishiopian.parrying.Registration.ModAttributes;
import com.theishiopian.parrying.Registration.ModCriteria;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class CommonEvents
{
    static float pAmount = 0;
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
       if(!event.getEntity().level.isClientSide)
       {
           LivingEntity entity = event.getEntityLiving();
           LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
           Parrying.Parry(event);
           float amount = event.getAmount();

           if(event.getSource() instanceof IndirectEntityDamageSource && event.getSource().isProjectile())
           {
               IndirectEntityDamageSource src = (IndirectEntityDamageSource) event.getSource();

               Entity e = src.getDirectEntity();

               if(e instanceof  AbstractArrowEntity)
               {
                   pAmount = amount;

                   entity.invulnerableTime = 0;
               }
           }

           if(attacker != null)
           {
                APItem weapon = attacker.getMainHandItem().getItem() instanceof APItem ? (APItem) attacker.getMainHandItem().getItem() : null;

                if(weapon != null && ArmorPenetration.IsNotBypassing())
                {
                    //noinspection OptionalGetWithoutIsPresent
                    float ap = (float) weapon.getAttributeModifiers(EquipmentSlotType.MAINHAND, attacker.getMainHandItem()).get(ModAttributes.AP.get()).stream().findFirst().get().getAmount();
                    ArmorPenetration.DoAPDamage(amount, ap, entity, attacker, weapon instanceof FlailItem, "bludgeoning.player");
                    event.setCanceled(true);
                }
           }
       }
    }

    public static void OnArrowImpact(ProjectileImpactEvent.Arrow event)
    {
        if(!Deflection.Deflect(event))
        {
            AbstractArrowEntity arrow = event.getArrow();

            if(arrow instanceof SpectralArrowEntity && Config.sonicSpectralArrow.get())
            {
                Vector3d pos = arrow.position();
                List<LivingEntity> entities = event.getArrow().level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos.x + 5, pos.y+5, pos.z+5, pos.x-5, pos.y-5, pos.z - 5));

                for(LivingEntity entity : entities)
                {
                    entity.addEffect(new EffectInstance(Effects.GLOWING, 100));
                }
            }

            if(arrow.isOnFire() && Config.flamingArrowGriefing.get())
            {
                World world = arrow.level;
                BlockPos blockPos = arrow.blockPosition();
                BlockState blockState = world.getBlockState(blockPos);

                if(CampfireBlock.canLight(blockState))
                {
                    world.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                }
                else
                {
                    if(event.getRayTraceResult() instanceof BlockRayTraceResult)
                    {
                        BlockRayTraceResult hit = ((BlockRayTraceResult)event.getRayTraceResult());

                        BlockPos pos = hit.getBlockPos().relative(hit.getDirection());

                        BlockState fireState = AbstractFireBlock.getState(world, pos);

                        world.setBlock(pos, fireState, 11);

                        if(arrow.getOwner() instanceof ServerPlayerEntity)ModCriteria.campfireLight.trigger((ServerPlayerEntity) arrow.getOwner());
                    }
                }
            }
        }
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;

        if(entity != null)
        {
            if(ArmorPenetration.IsNotBypassing() && Config.apPiercing.get())
            {
                if(event.getSource() instanceof IndirectEntityDamageSource && event.getSource().isProjectile())
                {
                    IndirectEntityDamageSource src = (IndirectEntityDamageSource) event.getSource();

                    Entity e = src.getDirectEntity();

                    if(e instanceof  AbstractArrowEntity)
                    {
                        AbstractArrowEntity arrow = (AbstractArrowEntity)e;

                        int pLevel = arrow.getPierceLevel();

                        if(pLevel > 0)
                        {
                            //it actually will bypass the shield, this is just to trick the helper method
                            ArmorPenetration.DoAPDamage(pAmount, 0.2f * pLevel, entity, attacker, false, "piercing.player");
                            event.setAmount(0);//prevent extra damage

                            //NOTE: the backstab still applies with this because the damage is applied separately inside DoAPDamage
                            //hence the need for a check if the system is doing AP
                        }

                        if(arrow instanceof ArrowEntity && Config.pickyPotionArrows.get())
                        {
                            List<EffectInstance> effects = ((ArrowEntity)arrow).potion.getEffects();

                            if(effects.size() > 0)
                            {
                                boolean hasHarm = false;
                                for (EffectInstance i : effects)
                                {
                                    if(!i.getEffect().isBeneficial())
                                    {
                                        hasHarm = true;
                                        break;
                                    }
                                }

                                if(!hasHarm)
                                {
                                    event.setAmount(0);
                                }
                            }
                        }
                    }
                }
            }

            if((!(entity instanceof PlayerEntity)) && entity.hasEffect(ModEffects.STUNNED.get()))
            {
                event.setAmount(event.getAmount() * 1.5f);
            }

            Backstab.DoBackstab(event, entity);
        }
    }

    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        Dodging.dodgeCooldown.replaceAll((k, v) -> v - 1);
        Dodging.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}