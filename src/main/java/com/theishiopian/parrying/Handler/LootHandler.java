package com.theishiopian.parrying.Handler;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.Items.QuiverItem;
import com.theishiopian.parrying.Items.ScabbardItem;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModLootModifiers;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
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
            add("dungeon_scabbard_modifier", ModLootModifiers.SCABBARD_MODIFIER.get(), new ScabbardModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:dungeon_scabbard"))
            );

            add("mineshaft_scabbard_modifier", ModLootModifiers.SCABBARD_MODIFIER.get(), new ScabbardModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/abandoned_mineshaft")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:mineshaft_scabbard"))
            );

            add("end_city_scabbard_modifier", ModLootModifiers.SCABBARD_MODIFIER.get(), new ScabbardModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/end_city_treasure")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:end_city_scabbard"))
            );

            add("nether_scabbard_modifier", ModLootModifiers.SCABBARD_MODIFIER.get(), new ScabbardModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/nether_bridge")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:nether_scabbard"))
            );

            add("dungeon_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:dungeon_quiver"))
            );

            add("mineshaft_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/abandoned_mineshaft")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:mineshaft_quiver"))
            );

            add("jungle_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/jungle_temple")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:jungle_quiver"))
            );

            add("desert_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/desert_pyramid")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:desert_quiver"))
            );

            add("igloo_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/igloo_pyramid")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:igloo_quiver"))
            );

            add("stronghold_crossing_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/stronghold_crossing")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:stronghold_quiver"))
            );

            add("stronghold_corridor_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/stronghold_corridor")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:stronghold_quiver"))
            );

            add("mansion_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("chests/woodland_mansion")).build(),
                            LootItemRandomChanceCondition.randomChance(0.25f).build()}, new ResourceLocation("parrying:mansion_quiver"))
            );

            add("skeleton_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("entities/skeleton")).build(),
                            LootItemKilledByPlayerCondition.killedByPlayer().build(),
                            LootItemRandomChanceCondition.randomChance(0.05f).build()}, new ResourceLocation("parrying:skeleton_quiver"))
            );

            add("stray_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("entities/stray")).build(),
                            LootItemKilledByPlayerCondition.killedByPlayer().build(),
                            LootItemRandomChanceCondition.randomChance(0.05f).build()}, new ResourceLocation("parrying:stray_quiver"))
            );

            add("pillager_quiver_modifier", ModLootModifiers.QUIVER_MODIFIER.get(), new QuiverModifier(
                    new LootItemCondition[] { LootTableIdCondition.builder(new ResourceLocation("entities/pillager")).build(),
                            LootItemKilledByPlayerCondition.killedByPlayer().build(),
                            LootItemRandomChanceCondition.randomChance(0.05f).build()}, new ResourceLocation("parrying:pillager_quiver"))
            );
        }
    }

    public static class QuiverModifier extends LootModifier
    {
        private final ResourceLocation table;
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

    public static class ScabbardModifier extends LootModifier
    {
        private final ResourceLocation tableLocation;
        /**
         * Constructs a LootModifier.
         *
         * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
         */
        protected ScabbardModifier(LootItemCondition[] conditionsIn, ResourceLocation tableLocation)
        {
            super(conditionsIn);
            this.tableLocation = tableLocation;
        }

        public LootItemCondition[] GetConditions()
        {
            return conditions;
        }

        @NotNull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
        {
            LootTable table = context.getLootTable(this.tableLocation);
            ItemStack scabbard = new ItemStack(ModItems.SCABBARD.get());
            ScabbardItem.AddLootSword(scabbard, table, context);
            generatedLoot.add(scabbard);
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<ScabbardModifier>
        {
            @Override
            public ScabbardModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions)
            {
                ResourceLocation table = new ResourceLocation(GsonHelper.getAsString(object, "scabbard_sword_table"));
                return new ScabbardModifier(conditions, table);
            }

            @Override
            public JsonObject write(ScabbardModifier instance)
            {
                JsonObject res = this.makeConditions(instance.GetConditions());
                res.addProperty("scabbard_sword_table", instance.tableLocation.toString());
                return res;
            }
        }
    }
}
