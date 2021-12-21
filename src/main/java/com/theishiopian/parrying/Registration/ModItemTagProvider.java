package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;


/**
 * This class is used in the dev environment to generate item tag data on the fly
 */
public class ModItemTagProvider extends ItemTagsProvider
{
    public ModItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generator, blockTagProvider, ParryingMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLD_SPEAR.get());
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLDEN_DAGGER.get());
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLD_FLAIL.get());
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLDEN_HAMMER.get());
        this.tag(ItemTags.PIGLIN_LOVED).add(ModItems.GOLDEN_MACE.get());
    }
}
