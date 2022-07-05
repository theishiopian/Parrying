package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ScabbardItem extends Item implements DyeableLeatherItem
{
    public ScabbardItem(Properties pProperties)
    {
        super(pProperties);
    }

    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(ScabbardCapability.class);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ScabbardCapability();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    private static ScabbardCapability getCapability(ItemStack scabbard)
    {
        if(!scabbard.is(ModItems.SCABBARD.get()))return null;
        LazyOptional<IItemHandler> handler = scabbard.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (handler.isPresent() && handler.orElse(null) instanceof ScabbardCapability capability) return capability;
        return null;
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack)
    {
        CompoundTag tag = super.getShareTag(stack)==null? stack.getOrCreateTag() : super.getShareTag(stack);
        ScabbardCapability c = getCapability(stack);
        if(c!=null && tag != null)
        {
            tag.put("scabbard", c.serializeNBT());
        }

        return tag;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt)
    {
        ScabbardCapability c = getCapability(stack);
        if(c!=null && nbt != null)
        {
            c.deserializeNBT(nbt.getCompound("scabbard"));
        }
        super.readShareTag(stack, nbt);
    }

    public void onDestroyed(ItemEntity pItemEntity)
    {
        Level level = pItemEntity.level;
        if(!level.isClientSide)
        {
            ScabbardCapability c = ScabbardItem.getCapability(pItemEntity.getItem());
            if(c == null || c.sword.isEmpty())return;
            level.addFreshEntity(new ItemEntity(level, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), c.sword.copy()));
            c.sword = ItemStack.EMPTY;
        }
    }

    public static boolean DoDrawAttack(Player player, ItemStack scabbard)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SWIFT_STRIKE.get(), scabbard);

        if(level > 0 && player.isCrouching() && !player.getCooldowns().isOnCooldown(ModItems.SCABBARD.get()))
        {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 5));
            player.swing(InteractionHand.MAIN_HAND, true);
            player.getCooldowns().addCooldown(ModItems.SCABBARD.get(), 600);
            player.level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_HURT, SoundSource.PLAYERS, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);

            List<Entity> targets = ParryModUtil.GetEntitiesInCone(player, 3, Config.swiftStrikeAngle.get());

            int attacks = 0;

            for (Entity target : targets)
            {
                player.attack(target);
                player.attackStrengthTicker = 1;
                attacks++;
                if(attacks >= 3) break;
            }

            player.resetAttackStrengthTicker();
            return true;
        }

        return false;
    }

    public static void DrawSword(Player player)
    {
        ItemStack itemToScan;
        ItemStack scabbard = ItemStack.EMPTY;
        ItemStack priorityScabbard = ItemStack.EMPTY;
        boolean lookingForEmpty = player.getMainHandItem().getItem() instanceof SwordItem;

        for(int i = 45; i >= 0; i--)
        {
            itemToScan = player.getInventory().getItem(i);
            boolean hasSword = ScabbardItem.HasSword(itemToScan);

            if(itemToScan.is(ModItems.SCABBARD.get()))
            {
                if(hasSword && lookingForEmpty || !hasSword && !lookingForEmpty) continue;
                scabbard = itemToScan;

                if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INTRUSIVE.get(), itemToScan) > 0)
                {
                    priorityScabbard = itemToScan;
                }
            }
        }

        if(!priorityScabbard.isEmpty())scabbard = priorityScabbard;

        if(!scabbard.isEmpty())
        {
            ScabbardCapability c = getCapability(scabbard);

            if(c != null)
            {
                if(player.getMainHandItem().getItem() instanceof SwordItem && c.sword.isEmpty())
                {
                    //sheathe
                    c.sword = player.getMainHandItem().copy();
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    playSheatheSound(player);
                }
                else if(!c.sword.isEmpty())
                {
                    //draw
                    ItemStack sword = c.sword.copy();
                    c.sword = ItemStack.EMPTY;
                    ItemStack oldItemInHand = player.getItemInHand(InteractionHand.MAIN_HAND).copy();
                    ItemEntity itemEntity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), oldItemInHand);
                    itemEntity.setNoPickUpDelay();
                    player.level.addFreshEntity(itemEntity);
                    player.setItemInHand(InteractionHand.MAIN_HAND, sword);
                    if(!DoDrawAttack(player, scabbard))playUnsheatheSound(player);
                }
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player)
    {
        if(!player.isCrouching())return super.onDroppedByPlayer(item, player);
        ScabbardCapability c = ScabbardItem.getCapability(item);
        if(c == null || c.sword.isEmpty())return true;
        player.drop(c.sword.copy(), true);
        c.sword = ItemStack.EMPTY;
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack scabbard, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        //Debug.log("stackedOnOther");
        ScabbardCapability c = getCapability(scabbard);
        if(c == null)return false;

        if (pAction != ClickAction.SECONDARY)
        {
            return false;
        }
        else
        {
            ItemStack toStackOnto = pSlot.getItem();
            if (toStackOnto.isEmpty() && c.sword != null)
            {
                playUnsheatheSound(pPlayer);
                pSlot.safeInsert(c.sword.copy());
                c.sword = ItemStack.EMPTY;
            }
            else if (toStackOnto.getItem() instanceof SwordItem)
            {
                c.sword = pSlot.safeTake(1,1,pPlayer);
            }
            else return false;

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack pStack, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess)
    {
        //Debug.log("stackedOnMe");
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
            ScabbardCapability c = getCapability(pStack);
            if(c == null)return false;

            if (pOther.isEmpty())
            {
                if(c.sword != null)
                {
                    playUnsheatheSound(pPlayer);
                    pAccess.set(c.sword.copy());
                    c.sword = ItemStack.EMPTY;
                }
            }
            else if(pOther.getItem() instanceof SwordItem)
            {
                playSheatheSound(pPlayer);
                c.sword = pAccess.get().copy();
                pAccess.set(ItemStack.EMPTY);
            }
            else return false;

            return true;
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext)
    {
        if(pContext.getLevel().isClientSide)return super.useOn(pContext);
        BlockPos pos = pContext.getClickedPos();
        BlockState block = pContext.getLevel().getBlockState(pos);
        if(block.is(Blocks.WATER_CAULDRON))
        {
            ItemStack scabbard = pContext.getItemInHand();
            if(((DyeableLeatherItem)scabbard.getItem()).hasCustomColor(scabbard))
            {
                ((DyeableLeatherItem)scabbard.getItem()).clearColor(scabbard);
                LayeredCauldronBlock.lowerFillLevel(block, pContext.getLevel(), pos);
                return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);
            }
            else return InteractionResult.PASS;
        }
        return super.useOn(pContext);
    }

    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add((new TranslatableComponent("filter.parrying.swords")).withStyle(ChatFormatting.GOLD));
        ScabbardCapability c = getCapability(pStack);
        if(c == null)return;

        if(c.sword.isEmpty())
        {
            pTooltipComponents.add((new TranslatableComponent("filter.parrying.none")).withStyle(ChatFormatting.DARK_RED));
            return;
        }
        List<Component> components = c.sword.getTooltipLines(pLevel.isClientSide ? Minecraft.getInstance().player : null, pIsAdvanced);

        pTooltipComponents.addAll(components);
    }

    public static boolean HasSword(ItemStack scabbard)
    {
        ScabbardCapability c = getCapability(scabbard);
        return c != null && !c.sword.isEmpty();
    }

    private static void playUnsheatheSound(Entity entity)
    {
        //Debug.log("playing sound");
        entity.level.playSound(null, entity.blockPosition(), ModSoundEvents.DRAW_SWORD.get(), SoundSource.PLAYERS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private static void playSheatheSound(Entity entity)
    {
        //Debug.log("playing sound");
        entity.level.playSound(null, entity.blockPosition(), ModSoundEvents.SHEATHE_SWORD.get(), SoundSource.PLAYERS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    static class ScabbardCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
    {
        public ItemStack sword = ItemStack.EMPTY;

        public ScabbardCapability()
        {
            super();
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag itemTag = new CompoundTag();
            sword.save(itemTag);
            CompoundTag nbt = new CompoundTag();
            nbt.put("Sword", itemTag);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            sword = ItemStack.of(nbt.getCompound("Sword"));
        }

        //borrowed from immersive engineering, modified for use here
        public static <T> LazyOptional<T> constantOptional(T val)
        {
            LazyOptional<T> result = LazyOptional.of(() -> Objects.requireNonNull(val));
            result.resolve();
            return result;
        }

        private final LazyOptional<ScabbardCapability> scabbardCapabilityLazyOptional = constantOptional(this);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
        {
            return scabbardCapabilityLazyOptional.cast();
        }
    }
}
