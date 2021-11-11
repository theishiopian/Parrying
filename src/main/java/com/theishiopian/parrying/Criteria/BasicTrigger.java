package com.theishiopian.parrying.Criteria;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BasicTrigger extends AbstractCriterionTrigger<BasicTrigger.Instance>
{
    private static ResourceLocation ID;

    public BasicTrigger(String name)
    {
        ID = new ResourceLocation(ParryingMod.MOD_ID, name);
    }

    @Override
    protected @NotNull Instance createInstance(@NotNull JsonObject fromJson, EntityPredicate.@NotNull AndPredicate predicate, @NotNull ConditionArrayParser conditionsParser)
    {
        return new Instance(predicate);
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return ID;
    }

    public void trigger(ServerPlayerEntity player)
    {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends  CriterionInstance
    {
        public Instance(EntityPredicate.AndPredicate predicate)
        {
            super(ID, predicate);
        }

        @SuppressWarnings("unused")
        public static Instance create()
        {
            return new Instance(EntityPredicate.AndPredicate.ANY);
        }
    }
}
