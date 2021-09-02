package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Items.APItem;
import com.theishiopian.parrying.Items.FlailItem;
import com.theishiopian.parrying.Mechanics.Backstab;
import com.theishiopian.parrying.Mechanics.Deflection;
import com.theishiopian.parrying.Mechanics.Dodging;
import com.theishiopian.parrying.Mechanics.Parrying;
import com.theishiopian.parrying.Registration.ModAttributes;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class CommonEvents
{
    //may want to make these some form of public, or make getters, for mod compat
    static boolean bypassing = false;
    static boolean piercing = false;
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

                if(weapon != null)
                {
                    float ap = (float) weapon.getAttributeModifiers(EquipmentSlotType.MAINHAND, attacker.getMainHandItem()).get(ModAttributes.AP.get()).stream().findFirst().get().getAmount();
                    DoAPDamage(amount, ap, entity, attacker, weapon instanceof FlailItem, "bludgeoning.player");
                    event.setCanceled(true);
                }
           }
       }
    }

    private static void DoAPDamage(float amount, float ap, LivingEntity entity, LivingEntity attacker, boolean bypassShield, String src)
    {
        if(!bypassing)
        {
            //ParryingMod.LOGGER.info("piercing");
            bypassing = true;
            float nonAP = 1 - ap;
            float dmgAP = amount * ap;
            float dmgNAP = amount * nonAP;

            entity.hurt(new EntityDamageSource(src, attacker), dmgNAP);
            entity.invulnerableTime = 0;
            if(!IsBlocked(entity, attacker))
            {
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), dmgAP);
            }
            else if(bypassShield)
            {
                //this is stupid
                //minecraft apparently has decided that armor and shields are the same thing, so bypassArmor is also used to bypass shields.
                //thus, I need to do all this math AGAIN
                float d = amount/2;
                float da = CombatRules.getDamageAfterAbsorb(d, (float)entity.getArmorValue(), (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
                //TODO: add shield effects
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), d * ap);
                entity.invulnerableTime = 0;
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), da * nonAP);

                BlockHelper(attacker, entity, amount / 2);
            }
            else
            {
                BlockHelper(attacker, entity, amount);
            }

            attacker.getMainHandItem().hurtAndBreak(1, attacker, (playerEntity) -> playerEntity.broadcastBreakEvent(attacker.getUsedItemHand()));

            bypassing = false;


        }
    }

    public static void OnArrowImpact(ProjectileImpactEvent.Arrow event)
    {
        if(!Deflection.Deflect(event))
        {
            AbstractArrowEntity arrow = event.getArrow();
            //ParryingMod.LOGGER.info(event.getEntity());
            if(arrow instanceof SpectralArrowEntity)
            {
                Vector3d pos = arrow.position();
                List<LivingEntity> entities = event.getArrow().level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos.x + 5, pos.y+5, pos.z+5, pos.x-5, pos.y-5, pos.z - 5));

                for(LivingEntity entity : entities)
                {
                    entity.addEffect(new EffectInstance(Effects.GLOWING, 100));
                }
            }

            if(arrow.isOnFire())
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
                    }
                }
            }
        }
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        //ParryingMod.LOGGER.info("Entity " + event.getEntity().getName().getString() + " took " + event.getAmount() + " damage");

        LivingEntity entity = event.getEntityLiving();
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
        float amount = event.getAmount();

        if(entity != null)
        {
            if(!piercing)
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
                            piercing = true;
                            DoAPDamage(pAmount, 0.2f * pLevel, entity, attacker, false, "piercing.player");
                            piercing = false;
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

    //debugging code, pls ignore
//    public static void OnTick(TickEvent.PlayerTickEvent event)
//    {
//        float swing = event.player.attackAnim;
//        int swingT = event.player.swingTime;
//
//       if(event.player instanceof ServerPlayerEntity)
//       {
//           ParryingMod.LOGGER.info("Anim: " + swing);
//           ParryingMod.LOGGER.info("Time: " + swingT);
//       }
//    }

    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        Dodging.dodgeCooldown.replaceAll((k, v) -> v - 1);
        Dodging.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    private static boolean IsBlocked(LivingEntity defender, LivingEntity attacker)
    {
        if (defender.isBlocking())
        {
            Vector3d attackPos = attacker.position();
            Vector3d defenderLook = defender.getViewVector(1.0F);
            Vector3d vector3d1 = attackPos.vectorTo(defender.position()).normalize();
            vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
            return vector3d1.dot(defenderLook) < 0.0D;
        }

        return false;
    }

    private static void BlockHelper(LivingEntity toBlock, LivingEntity blocker, float blockedDMG)
    {
        toBlock.knockback(0.5F, toBlock.getX() - blocker.getX(), toBlock.getZ() - blocker.getZ());
        blocker.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + blocker.level.random.nextFloat() * 0.4F);

        if(blocker instanceof ServerPlayerEntity)
        {
            ItemStack shield = blocker.getUseItem();
            ((ServerPlayerEntity)blocker).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(blockedDMG * 10.0F));
            ((ServerPlayerEntity)blocker).awardStat(Stats.ITEM_USED.get(shield.getItem()));

            if (blockedDMG >= 3.0F)
            {
                int i = 1 + MathHelper.floor(blockedDMG);
                Hand hand = blocker.getUsedItemHand();
                blocker.getUseItem().hurtAndBreak(i, blocker, (entity) ->
                {
                    entity.broadcastBreakEvent(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((PlayerEntity) blocker, shield, hand);
                });
                if (shield.isEmpty())
                {
                    if (hand == Hand.MAIN_HAND)
                    {
                        blocker.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    }
                    else
                    {
                        blocker.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    }

                    blocker.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + blocker.level.random.nextFloat() * 0.4F);
                }
            }
        }
    }
}