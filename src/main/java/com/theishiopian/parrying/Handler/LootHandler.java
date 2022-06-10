package com.theishiopian.parrying.Handler;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.Items.QuiverItem;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModLootModifiers;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
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
            add("dungeon_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:dungeon_quiver"))
            );
        }
    }

    public static class QuiverModifier extends LootModifier
    {
        private ResourceLocation table;
        /**
         * Constructs a LootModifier.
         *
         * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
         */
        protected QuiverModifier(LootItemCondition[] conditionsIn, ResourceLocation table)
        {
            super(conditionsIn);
            this.table = table;
        }

        public LootItemCondition[] GetConditions()
        {
            return conditions;
        }

        @NotNull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
        {
//            ItemStack quiver = new ItemStack(ModItems.QUIVER.get());
//            quiver.setCount(1);
//            QuiverItem.AddRandomArrows(quiver, (ArrowItem) Items.ARROW, null, 0.5f, 64);
//            QuiverItem.AddRandomArrows(quiver, (ArrowItem) Items.SPECTRAL_ARROW, null, 0.15f, 32);
//            QuiverItem.AddRandomArrows(quiver, (ArrowItem) Items.TIPPED_ARROW, Potions.POISON, 0.25f, 8);
//            QuiverItem.AddRandomArrows(quiver, (ArrowItem) Items.TIPPED_ARROW, Potions.HEALING, 0.15f, 16);

            LootTable table = context.getLootTable(this.table);
            ItemStack quiver = new ItemStack(ModItems.QUIVER.get());
            quiver.setCount(1);
            QuiverItem.AddLootArrows(quiver, table, context);
            generatedLoot.add(quiver);
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<QuiverModifier>
        {
            @Override
            public QuiverModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions)
            {
                ResourceLocation table = new ResourceLocation(GsonHelper.getAsString(object, "quiver_arrows_table"));
                return new QuiverModifier(conditions, table);
            }

            @Override
            public JsonObject write(QuiverModifier instance)
            {
                JsonObject res = this.makeConditions(instance.GetConditions());
                res.addProperty("quiver_arrows_table", instance.table.toString());
                return res;
            }
        }
    }
}
