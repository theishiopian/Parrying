package com.theishiopian.parrying.Handler;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.Items.QuiverItem;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModLootModifiers;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LootHandler
{
    public static class DataProvider extends GlobalLootModifierProvider
    {
        public DataProvider(DataGenerator gen, String modId)
        {
            super(gen, modId);
        }

        @Override
        protected void start()
        {
            add("quiver_modifier", ModLootModifiers.QUIVER_DUNGEON_LOOT.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build() })
            );
        }
    }

    public static class QuiverModifier extends LootModifier
    {
        /**
         * Constructs a LootModifier.
         *
         * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
         */
        protected QuiverModifier(LootItemCondition[] conditionsIn)
        {
            super(conditionsIn);
        }

        public LootItemCondition[] GetConditions()
        {
            return conditions;
        }

        @NotNull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
        {
            Debug.log("do apply");
            ItemStack quiver = new ItemStack(ModItems.QUIVER.get());
            quiver.setCount(1);
            QuiverItem.AddRandomArrows(quiver);
            generatedLoot.add(quiver);
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<QuiverModifier>
        {
            @Override
            public QuiverModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions)
            {
                return new QuiverModifier(conditions);
            }

            @Override
            public JsonObject write(QuiverModifier instance)
            {
                return this.makeConditions(instance.GetConditions());
            }
        }
    }
}
