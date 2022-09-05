package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.item.alchemy.PotionBrewing.*;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin
{
    @Inject(method = "bootStrap", at = @At("HEAD"), cancellable = true)
    private static void ReplaceRecipes(CallbackInfo ci)
    {
        //todo config here

        //boiler plate
        addContainer(Items.POTION);
        addContainer(Items.SPLASH_POTION);
        addContainer(Items.LINGERING_POTION);

        addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        addContainerRecipe(Items.POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        //todo oil goes here

        //base potions
        addMix(Potions.WATER, Items.WHEAT, Potions.THICK);
        addMix(Potions.THICK, Items.ROTTEN_FLESH, Potions.MUNDANE);
        addMix(Potions.MUNDANE, Items.NETHER_WART, Potions.AWKWARD);

        //tier 0
        addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);

        //tier 1
        addMix(Potions.THICK, Items.SPIDER_EYE, Potions.POISON);
        addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);

        addMix(Potions.THICK, Items.SUGAR, Potions.SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);

        addMix(Potions.THICK, Items.STRING, Potions.SLOWNESS);
        addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);

        addMix(Potions.THICK, Items.POISONOUS_POTATO, Potions.WEAKNESS);
        addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);

        //tier 2
        addMix(Potions.MUNDANE, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);

        addMix(Potions.MUNDANE, Items.RABBIT_FOOT, Potions.LEAPING);
        addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);

        addMix(Potions.MUNDANE, Items.PUFFERFISH, Potions.WATER_BREATHING);
        addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);

        addMix(Potions.MUNDANE, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);

        //TODO anchoring potion goes here

        //tier 3
        addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);

        addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);

        addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);

        addMix(Potions.AWKWARD, Items.TROPICAL_FISH, Potions.INVISIBILITY);
        addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);

        addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);

        //TODO mortality potion goes here

        ci.cancel();
    }
}
