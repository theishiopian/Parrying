package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.*;
import com.theishiopian.parrying.Mechanics.*;
import com.theishiopian.parrying.Network.GameplayStatusPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Trades.DyedItemForEmeralds;
import com.theishiopian.parrying.Utility.ModUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathResourcePack;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CommonEvents
{
    static float pAmount = 0;//this is dumb
    static float strength = 0;//so is this
    //these static fields transfer information between layers of method calls, in between which this information is altered or destroyed. The only other way of ensuring access
    //is to restructure the logic of the minecraft combat system, which would cause innumerable problems for compatibility im sure.
    //Why do you do this to me mojang?

    //private static final HashMap<UUID, Provided> provisions = new HashMap<>();
    private static Provided localProvided;

    protected record Provided(ItemStack provided, InteractionHand hand){}

    public static void OnPlayerAttackTarget(AttackEntityEvent event)
    {
        strength = event.getPlayer().getAttackStrengthScale(0.5f);

        if(!event.getPlayer().level.isClientSide )
        {
            var mainItem = event.getPlayer().getMainHandItem();

            if(mainItem.getItem() instanceof SpearItem)
            {
                float dist = (float) event.getPlayer().position().distanceTo(event.getTarget().position());

                if(dist > 3)
                {
                    ModTriggers.poke.trigger((ServerPlayer) event.getPlayer());
                }
            }
        }
    }

    public static void OnRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        AbstractBundleItem.registerCapability(event);
        ScabbardItem.registerCapability(event);
    }

    public static void OnArrowShoot(EntityJoinWorldEvent event)
    {
        ArrowMechanics.DoZeroGravityBolts(event.getEntity());
    }

    public static void OnArrowScan(LivingGetProjectileEvent event)
    {
        if(event.getEntityLiving() instanceof Player player &&
                (event.getProjectileWeaponItemStack().getItem() instanceof BowItem ||
                        (event.getProjectileWeaponItemStack().getItem() instanceof CrossbowItem)))
        {
            var stack = QuiverItem.ScanForArrows(player);
            if(stack != null)event.setProjectileItemStack(stack);
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
                if(ModUtil.IsBlocked(player, attacker))
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

    public static void OnProjectileImpact(ProjectileImpactEvent event)
    {
        if(DeflectionMechanic.Deflect(event)) return;

        if(event.getProjectile() instanceof AbstractArrow arrow)
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
            if(!OilMechanics.DoMeleeOil(weapon, target, attacker))event.setAmount(0);

            float diff = target.getHealth() - event.getAmount();
            if(target.hasEffect(ModEffects.IMMORTALITY.get()) && !event.getSource().isBypassInvul() && (diff <= 0 || target.getHealth() <= 2))
            {
                event.setAmount(0);
                target.setHealth(2);
                target.level.playSound(null, target.blockPosition(), ModSoundEvents.IMMORTAL_HIT.get(), SoundSource.NEUTRAL, 0.4F, 0.8F + target.getLevel().getRandom().nextFloat() * 0.2F);

                if(Math.abs(diff) >= 10 && target instanceof ServerPlayer player)
                {
                    ModTriggers.immortal.trigger(player);
                }
            }

            if(event.getSource().isFall() && target.hasEffect(ModEffects.FORTIFIED.get()))
            {
                event.setAmount(event.getAmount() * 0.8f);//milk reduces fall damage by 20%, because bones
            }

            if(event.getSource() instanceof IndirectEntityDamageSource src && event.getSource().isProjectile())
            {
                if(src.getDirectEntity() instanceof AbstractArrow arrow)
                {
                    if(!ArrowMechanics.DoAPBolts(arrow, target, attacker, pAmount, strength))event.setAmount(0);
                    if(!ArrowMechanics.DoPickyPotionProjectiles(arrow, target)) event.setAmount(0);
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

            BackstabMechanic.DoBackstab(event, target);
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

        DodgingMechanic.dodgeCooldown.replaceAll((k, v) -> v - 1);
        DodgingMechanic.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);

        ScabbardItem.drawCooldown.replaceAll((k, v) -> v - 1);
        ScabbardItem.drawCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
        GameplayStatusPacket.UpdateTicks();
    }

    public static void OnPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(!event.player.level.isClientSide())
        {
            ParryingMechanic.DoParryTick((ServerPlayer) event.player);
        }
        else if(localProvided != null)
        {
            event.player.setItemInHand(localProvided.hand, localProvided.provided);
            localProvided = null;
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
                ModTriggers.rally.trigger(player);//todo horn
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

    public static void OnPotionEffectAdded(PotionEvent.PotionApplicableEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        var effect = event.getPotionEffect();

        if(!AntidoteMechanic.DoAntidoteCheck(entity, effect)) event.setResult(Event.Result.DENY);
    }

    public static void OnLivingTick(LivingEvent.LivingUpdateEvent event)
    {
        if(Config.potionSickness.get())
        {
            LivingEntity entity = event.getEntityLiving();
            if(entity.getActiveEffects().size() > Config.potionTolerance.get() && !entity.hasEffect(ModEffects.FORTIFIED.get()))
            {
                entity.removeAllEffects();
                if(Config.potionSicknessNausea.get())entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300));

                if(entity instanceof ServerPlayer player)ModTriggers.sick.trigger(player);
            }
        }
    }

    public static void OnHeal(LivingHealEvent event)
    {
        if(event.getEntityLiving().hasEffect(MobEffects.WITHER) && Config.witherRework.get())
        {
            event.setCanceled(true);
        }
        else if(event.getEntityLiving().hasEffect(ModEffects.VITALITY.get()))
        {
            event.setAmount(event.getAmount() * 1.5f);
        }
    }

    public static void OnEatStew(LivingEntityUseItemEvent.Finish event)
    {
        var item = event.getItem();
        if(item.is(ModTags.STEW))//todo stew config
        {
            event.getEntityLiving().addEffect(new MobEffectInstance(ModEffects.STUFFED.get(), 2400));
        }
    }

    public static void OnRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        var level = event.getWorld();
        if(level.isClientSide) return;
        var player = event.getPlayer();
        var weapon = player.getItemInHand(event.getHand());
        var pos = event.getPos();
        var blockState = player.level.getBlockState(pos);

        if(blockState.is(Blocks.WATER_CAULDRON))
        {
            if(ModUtil.IsStackWeapon(weapon) && !PotionUtils.getMobEffects(weapon).isEmpty())
            {
                weapon.removeTagKey("CustomPotionColor");
                weapon.removeTagKey("Potion");
                player.level.playSound(null, player.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.PLAYERS, 0.4F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.2F);
                LayeredCauldronBlock.lowerFillLevel(blockState, level, pos);
            }
        }
    }

    public static void OnRegisterWanderingTrades(WandererTradesEvent event)
    {
        var common = event.getGenericTrades();
        var rare = event.getRareTrades();

        rare.add(new BasicItemListing(10, PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SUSTENANCE.get()), 1, 12));
        rare.add(new BasicItemListing(5, PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.BEES.get()), 5, 12));
    }

    public static void OnRegisterTrades(VillagerTradesEvent event)
    {
        if(Config.quiverEnabled.get() && event.getType() == VillagerProfession.FLETCHER)
        {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            //trade offer
            //I receive, quiver
            // you receive, emeralds
            trades.get(1).add(new DyedItemForEmeralds(ModItems.QUIVER.get(), 5, 1));
            trades.get(2).add(new DyedItemForEmeralds(ModItems.QUIVER.get(), 5, 2));
            trades.get(3).add(new DyedItemForEmeralds(ModItems.QUIVER.get(), 5, 3));
            trades.get(4).add(new DyedItemForEmeralds(ModItems.QUIVER.get(), 5, 4));
            trades.get(5).add(new DyedItemForEmeralds(ModItems.QUIVER.get(), 5, 5));
        }

        if(Config.scabbardEnabled.get() && event.getType() == VillagerProfession.WEAPONSMITH)
        {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            //trade offer
            //I receive, scabbard
            // you receive, emeralds
            trades.get(1).add(new DyedItemForEmeralds(ModItems.SCABBARD.get(), 5, 1));
            trades.get(2).add(new DyedItemForEmeralds(ModItems.SCABBARD.get(), 5, 2));
            trades.get(3).add(new DyedItemForEmeralds(ModItems.SCABBARD.get(), 5, 3));
            trades.get(4).add(new DyedItemForEmeralds(ModItems.SCABBARD.get(), 5, 4));
            trades.get(5).add(new DyedItemForEmeralds(ModItems.SCABBARD.get(), 5, 5));
        }

        if(event.getType() == VillagerProfession.CLERIC)
        {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            //potion seller, i require your strongest potions
            trades.get(1).add(new BasicItemListing(5, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), 5, 1));
            trades.get(1).add(new BasicItemListing(7, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING), 3, 3));
            trades.get(1).add(new BasicItemListing(7, PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CLEANSING.get()), 3, 3));
            trades.get(2).add(new BasicItemListing(8, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.REGENERATION), 1, 5));
            trades.get(3).add(new BasicItemListing(10, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.CLEANSING.get()), 1, 7));
            trades.get(5).add(new BasicItemListing(10, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_STRENGTH), 1, 8));

            trades.get(1).add(new DyedItemForEmeralds(ModItems.BANDOLIER.get(), 5, 1));
            trades.get(2).add(new DyedItemForEmeralds(ModItems.BANDOLIER.get(), 5, 2));
            trades.get(3).add(new DyedItemForEmeralds(ModItems.BANDOLIER.get(), 5, 3));
            trades.get(4).add(new DyedItemForEmeralds(ModItems.BANDOLIER.get(), 5, 4));
            trades.get(5).add(new DyedItemForEmeralds(ModItems.BANDOLIER.get(), 5, 5));
        }
    }

    /**
     * This mess is responsible for creating the resource pack that covers up the brewing stand fuel slot.
     * @param event The event from forge.
     */
    public static void OnAddPackFinders(AddPackFindersEvent event)
    {
        try
        {
            if (event.getPackType() == PackType.CLIENT_RESOURCES && !Config.brewingRequiresFuel.get())
            {
                var resourcePath = ModList.get().getModFileById(ParryingMod.MOD_ID).getFile().findResource("brewing_stand_optional");
                var pack = new PathResourcePack(ModList.get().getModFileById(ParryingMod.MOD_ID).getFile().getFileName() + ":" + resourcePath, resourcePath);
                var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadataSection != null)
                {
                    event.addRepositorySource((packConsumer, packConstructor) ->
                            packConsumer.accept(packConstructor.create(
                                    "builtin/parrying", new TextComponent("Brewing Stand Reskin"), true, //true makes it required
                                    () -> pack, metadataSection, Pack.Position.TOP, PackSource.BUILT_IN, true)));//true makes it be hidden
                }
            }
        }
        catch(IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}