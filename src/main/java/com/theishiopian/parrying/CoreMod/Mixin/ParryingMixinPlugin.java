package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ParryingMixinPlugin implements IMixinConfigPlugin
{
    private static final String[] CLIENT_MIXINS = new String[]
    {
        "ItemInHandRendererMixin",
        "BrewingStandGUIMixin"
    };

    private static final String[] COMMON_MIXINS = new String[]
    {
        "PlayerMixin",
        "TiersMixin",
        "ArmorMaterialsMixin",
        "ArrowMixin",
        "PotionItemMixin",
        "ThrowablePotionItemMixin",
        "MilkBucketMixin",
        "ThrownPotionEntityMixin",
        "PoisonMixin",
        "PotionBrewingMixin",
        "BrewingStandMixin",
        "BrewingStandMenuMixin",
        "BrewingStandFuelSlotMixin",
        "FoodDataMixin",
        "FoodsMixin"
    };

    @Override
    public void onLoad(String mixinPackage)
    {

    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {

    }

    @Override
    public List<String> getMixins()
    {
        ArrayList<String> mixins = new ArrayList<>();

        if(FMLEnvironment.dist == Dist.CLIENT)
        {
            mixins.addAll(Arrays.asList(CLIENT_MIXINS));
        }
        mixins.addAll(Arrays.asList(COMMON_MIXINS));

        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }
}
