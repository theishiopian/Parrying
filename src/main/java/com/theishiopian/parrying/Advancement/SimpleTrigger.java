package com.theishiopian.parrying.Advancement;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SimpleTrigger extends SimpleCriterionTrigger<SimpleTrigger.Instance>
{
    final ResourceLocation ID;
    @Override
    public SimpleTrigger.Instance createInstance(JsonObject pJson, EntityPredicate.Composite pEntityPredicate, DeserializationContext pConditionsParser)
    {
        return new SimpleTrigger.Instance(getId());
    }

    public void trigger(String idIn, ServerPlayer pPlayer)
    {
//        Debug.log("trigger received, checking...");
//        Debug.log(ID.toString());
//        Debug.log(idIn);
        boolean match = ID.toString().equals(idIn);
        Debug.log(match);
        if(match)this.trigger(pPlayer, (in)->true);
    }

    public SimpleTrigger(ResourceLocation id)
    {
        ID = id;
    }

    public ResourceLocation getId() {
        return ID;
    }

    public Instance instance() {
        return new Instance(ID);
    }


    public static class Instance extends AbstractCriterionTriggerInstance
    {
        public Instance(ResourceLocation idIn)
        {
            super(idIn, EntityPredicate.Composite.ANY);
        }

        public JsonObject serializeToJson(SerializationContext pConditions)
        {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            return jsonobject;
        }
    }
}
