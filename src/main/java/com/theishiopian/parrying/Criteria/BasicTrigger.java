package com.theishiopian.parrying.Criteria;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class BasicTrigger extends SimpleCriterionTrigger<BasicTrigger.Instance>
{
    private static ResourceLocation ID;

    public BasicTrigger(String name)
    {
        ID = new ResourceLocation(ParryingMod.MOD_ID, name);
    }

    @Override
    protected @NotNull Instance createInstance(@NotNull JsonObject fromJson, EntityPredicate.@NotNull Composite predicate, @NotNull DeserializationContext conditionsParser)
    {
        return new Instance(predicate);
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return ID;
    }

    public void trigger(ServerPlayer player)
    {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        public Instance(EntityPredicate.Composite predicate)
        {
            super(ID, predicate);
        }

        @SuppressWarnings("unused")
        public static Instance create()
        {
            return new Instance(EntityPredicate.Composite.ANY);
        }
    }
}
