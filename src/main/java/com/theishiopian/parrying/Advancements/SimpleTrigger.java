package com.theishiopian.parrying.Advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SimpleTrigger extends SimpleCriterionTrigger<SimpleTrigger.Instance>
{
    final ResourceLocation ID;
    @Override
    public SimpleTrigger.@NotNull Instance createInstance(@NotNull JsonObject pJson, EntityPredicate.@NotNull Composite pEntityPredicate, @NotNull DeserializationContext pConditionsParser)
    {
        return new SimpleTrigger.Instance(getId());
    }

    public void trigger(ServerPlayer pPlayer)
    {
        this.trigger(pPlayer, (in)->true);
    }

    public SimpleTrigger(ResourceLocation id)
    {
        ID = id;
    }

    public @NotNull ResourceLocation getId() {
        return ID;
    }


    public static class Instance extends AbstractCriterionTriggerInstance
    {
        public Instance(ResourceLocation idIn)
        {
            super(idIn, EntityPredicate.Composite.ANY);
        }

        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext pConditions)
        {
            return super.serializeToJson(pConditions);
        }
    }
}
