package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.*;
import com.theishiopian.parrying.Mechanics.*;
import com.theishiopian.parrying.Network.SyncDefPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class CommonEvents
{
    static float pAmount = 0;//this is dumb
    static float strength = 0;//so is this
    //these static fields transfer information between layers of method calls, in between which this information is altered or destroyed. The only other way of ensuring access
    //is to restructure the logic of the minecraft combat system, which would cause innumerable problems for compatibility im sure.
    //Why do you do this to me mojang?
    public static void OnPlayerAttackTarget(AttackEntityEvent event)
    {
        strength = event.getPlayer().getAttackStrengthScale(0.5f);

        if(!event.getPlayer().level.isClientSide && event.getPlayer().getMainHandItem().getItem() instanceof SpearItem)
        {
            float dist = (float) event.getPlayer().position().distanceTo(event.getTarget().position());

            if(dist > 3)
            {
                ModTriggers.poke.trigger((ServerPlayer) event.getPlayer());
            }
        }
    }

    public static void OnRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        QuiverItem.registerCapability(event);
    }

    public static void OnArrowShoot(EntityJoinWorldEvent event)
    {
        if
        (
            Config.zeroGravityBolts.get() &&
            event.getEntity() instanceof AbstractArrow arrow &&
            arrow.getOwner() instanceof LivingEntity shooter &&
            shooter.getMainHandItem().is(ModItems.SCOPED_CROSSBOW.get())
        )
        {
            arrow.setNoGravity(true);
        }
    }

    public static void OnArrowScan(LivingGetProjectileEvent event)
    {
        if(event.getEntityLiving() instanceof ServerPlayer player &&
                (event.getProjectileWeaponItemStack().getItem() instanceof BowItem ||
                (event.getProjectileWeaponItemStack().getItem() instanceof CrossbowItem)))
        {
            for (ItemStack item : player.getInventory().items)
            {
                if(item.is(ModItems.QUIVER.get()) && QuiverItem.GetCount(item) > 0)
                {
                    event.setProjectileItemStack(QuiverItem.PeekFirstStack(item));
                    break;
                }
            }
        }
    }

    public static void OnAttacked(LivingAttackEvent event)
    {
        if(!event.getEntity().level.isClientSide)
        {
            if(Config.protectPets.get() && event.getSource() instanceof EntityDamageSource src && event.getEntity() instanceof OwnableEntity pet && pet.getOwner() == src.getEntity())
            {
                event.setCanceled(true);
                return;
            }

            LivingEntity entity = event.getEntityLiving();
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
            float amount = event.getAmount();

            if(entity instanceof Player player)
            {
                if(ParryModUtil.IsBlocked(player, attacker))
                {
                    player.level.playSound(player, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level.random.nextFloat() * 0.4F);
                    float shieldAbsorb = 2; //TODO store in shields somehow
                    float amountPostAbsorb = Mth.clamp(amount - shieldAbsorb, 0, player.getMaxHealth());

                    float reduction = amountPostAbsorb / player.getMaxHealth();
                    UUID id = player.getUUID();
                    float oldValue = ParryingMechanic.ServerDefenseValues.get(id);
                    ParryingMechanic.ServerDefenseValues.replace(id, oldValue - reduction);
                }
                else ParryingMechanic.Parry(event, player);
            }

            if(event.getSource() instanceof IndirectEntityDamageSource src && event.getSource().isProjectile())
            {
                Entity e = src.getDirectEntity();

                if(e instanceof AbstractArrow)
                {
                    pAmount = amount;

                    entity.invulnerableTime = 0;
                }
            }

            if(attacker != null)
            {
                ItemStack weapon = attacker.getMainHandItem();

                if(weapon.getItem() instanceof BludgeonItem && ArmorPenetrationMechanic.IsNotBypassing())
                {
                    //yes, the attribute is there, I put it there
                    float ap = (float) weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).get(ModAttributes.AP.get()).stream().findFirst().get().getAmount();
                    ArmorPenetrationMechanic.DoAPDamage(amount, strength, ap, entity, attacker, weapon.getItem() instanceof FlailItem, "bludgeoning.player");
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void OnArrowImpact(ProjectileImpactEvent event)
    {
        if(event.getProjectile() instanceof AbstractArrow arrow && !Deflection.Deflect(event))
        {
            ArrowMechanics.DoSonicArrow(arrow);
            ArrowMechanics.DoBurningArrow(arrow, event.getRayTraceResult());
            ArrowMechanics.DoSnipeChallenge(arrow, event.getRayTraceResult());
        }
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        LivingEntity target = event.getEntityLiving();
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
        ItemStack weapon = attacker != null ? attacker.getMainHandItem() : ItemStack.EMPTY;

        if(target != null)
        {
            if(Config.apPiercing.get() && ArmorPenetrationMechanic.IsNotBypassing())
            {
                if(event.getSource() instanceof IndirectEntityDamageSource src && event.getSource().isProjectile())
                {

                    Entity e = src.getDirectEntity();

                    if(e instanceof AbstractArrow arrow)
                    {
                        int pLevel = arrow.getPierceLevel();

                        if(pLevel > 0)
                        {
                            //it actually will bypass the shield, this is just to trick the helper method
                            ArmorPenetrationMechanic.DoAPDamage(pAmount,strength, 0.2f * pLevel, target, attacker, false, "piercing.player");
                            event.setAmount(0);//prevent extra damage

                            //NOTE: the backstab still applies with this because the damage is applied separately inside DoAPDamage
                            //hence the need for a check if the system is doing AP
                        }

                        if(arrow instanceof Arrow && Config.pickyPotionArrows.get())
                        {
                            List<MobEffectInstance> effects = ((Arrow)arrow).potion.getEffects();

                            if(effects.size() > 0)
                            {
                                boolean shouldBeHarmful = true;
                                for (MobEffectInstance i : effects)
                                {
                                    boolean beneficial = i.getEffect().isBeneficial();
                                    boolean isInstantHeal = i.getEffect() == MobEffects.HEAL;
                                    boolean isInstantHarm = i.getEffect() == MobEffects.HARM;
                                    boolean targetUndead = target.getMobType() == MobType.UNDEAD;
                                    if(beneficial && (!targetUndead && isInstantHeal))
                                    {
                                        shouldBeHarmful = false;
                                        break;
                                    }
                                    else if(targetUndead && isInstantHarm)
                                    {
                                        shouldBeHarmful = false;
                                        break;
                                    }
                                }

                                if(!shouldBeHarmful)
                                {
                                    event.setAmount(0);
                                }
                            }
                        }
                    }
                }
            }

            if((!(target instanceof Player)) && target.hasEffect(ModEffects.STUNNED.get()))
            {
                event.setAmount(event.getAmount() * 1.5f);
            }

            int joustLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.JOUSTING.get(), weapon);

            if(weapon.getItem() instanceof SpearItem)
            {
                assert attacker != null;//if attacker is null weapon is empty
                if (attacker.isPassenger() && joustLevel > 0)
                {
                    event.setAmount(event.getAmount() + 2 * joustLevel);
                }
            }

            if(attacker != null && Config.cripplingEnchantEnabled.get() && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.CRIPPLING.get(), attacker) > 0)
            {
                float chance = ParryModUtil.random.nextFloat();

                if(chance <= 0.25)
                {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) Math.floor(chance * 20 * 4) + 20));
                }
            }

            Backstab.DoBackstab(event, target);
        }
    }

    public static void OnPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayer player) ParryingMechanic.ServerDefenseValues.putIfAbsent(player.getUUID(), 1f);
    }

    public static void OnPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayer player) ParryingMechanic.ServerDefenseValues.remove(player.getUUID());
    }

    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        Dodging.dodgeCooldown.replaceAll((k, v) -> v - 1);
        Dodging.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    public static void OnPlayerTick(TickEvent.PlayerTickEvent event)
    {
//        float value = event.player.level.isClientSide ? ParryingMechanic.ClientDefense : ParryingMechanic.ServerDefenseValues.get(event.player.getUUID());
//        if (value < 1)
//        {
//            Debug.log("PLAYER TICK--------------------------------");
//            String side = event.player.level.isClientSide ? "Client " : "Server ";
//            Debug.log(side + " Value for" + event.player.getName().getString() + ":" + value);
//            Debug.log("END TICK------------------------------------");
//        }

        if(!event.player.level.isClientSide())
        {
            if(!ParryModUtil.IsWeapon(event.player.getMainHandItem()) && ParryModUtil.IsWeapon(event.player.getOffhandItem()))
            {
                DualWielding.dualWielders.remove(event.player.getUUID());
            }
            float newValue;
            float v = ParryingMechanic.ServerDefenseValues.get(event.player.getUUID());
            if(v <= 0)
            {
                event.player.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 60));
                float pitch = ParryModUtil.random.nextFloat() * 0.4f + 0.8f;
                event.player.level.playSound(null, event.player.blockPosition(), ModSoundEvents.DEFENSE_BREAK.get(), SoundSource.PLAYERS, 1f, pitch);
                Vec3 pos = event.player.position();

                ((ServerLevel) event.player.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 30, 0.5D, 2D, 0.5D, 0.0D);
                Vec3 dir = event.player.getViewVector(1);
                event.player.knockback(1, dir.x, dir.z);
                event.player.hurtMarked = true;

                ModTriggers.stagger.trigger((ServerPlayer) event.player);

                if (event.player.isBlocking())
                {
                    event.player.getCooldowns().addCooldown(event.player.getUseItem().getItem(), 60);
                    event.player.stopUsingItem();
                    event.player.level.playSound(null, event.player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1f,1f);
                }
                newValue = 0.001f;
            }
            else if(v < 1)
            {
                newValue = v + 0.003f;
            }
            else
            {
                newValue = 1f;
            }

            ParryingMechanic.ServerDefenseValues.replace(event.player.getUUID(), newValue);

            ParryingMod.channel.send(PacketDistributor.PLAYER.with(()-> (ServerPlayer) event.player), new SyncDefPacket(newValue));
        }
    }

    public static void OnDeath(LivingDeathEvent event)
    {
        if(event.getSource().getEntity() instanceof ServerPlayer player)
        {
            if(event.getEntityLiving().hasEffect(ModEffects.STUNNED.get()) && player.getHealth() < 5)ModTriggers.retribution.trigger(player);

            if
            (
                event.getEntityLiving() instanceof Pig
                && event.getSource() instanceof IndirectEntityDamageSource source
                && source.getDirectEntity() instanceof AbstractArrow arrow
                && arrow.isOnFire()
            )
            {
                ModTriggers.bacon.trigger(player);
            }

            if(player.getMainHandItem().getItem() instanceof FlailItem && player.hasEffect(MobEffects.ABSORPTION))
            {
                ModTriggers.rally.trigger(player);
            }

            //TODO: convert this to json?
            if
            (
                player.getMainHandItem().getItem() instanceof SpearItem
                && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
                && player.isPassenger()
                && player.getVehicle() instanceof Horse
            )
            {
                ModTriggers.hussars.trigger(player);
            }
        }
    }

    /**
     * VIBE CHECK
     * @param event the event
     */
    public static void OnHitBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getWorld().getBlockState(event.getPos()).is(Blocks.BEDROCK) && !event.getPlayer().isCreative() && event.getPlayer().getMainHandItem().getItem() instanceof HammerItem)
        {
            event.setCanceled(true);
            Player player = event.getPlayer();
            BlockPos bPos = event.getPos();
            Vec3 pos = new Vec3(bPos.getX() + 0.5f, bPos.getY() + 0.5f, bPos.getZ() + 0.5f);
            Vec3 dir = (pos.subtract(player.position())).normalize();

            player.knockback(0.2f, dir.x, dir.z);
            player.hurtMarked = true;
            player.hurt(ModDamageSources.BEDROCK, 1);
            player.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 60));
            if(player instanceof ServerPlayer serverPlayer)ModTriggers.vibe.trigger(serverPlayer);
        }
    }
}