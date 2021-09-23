package com.theishiopian.parrying.Recipes;

import com.google.gson.JsonObject;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Supplier;

/**
 * This class defines an object that in turn defines a recipe condition with a simple boolean value. The exact value is defined via supplier.
 * See ParryingMod.java for registration.
 */
public class EnabledCondition implements ICondition
{
    private final ResourceLocation name;//the name of the condition, eg "parrying:flails_enabled"
    private final Supplier<Boolean> condition;//the condition to evaluate

    @SuppressWarnings("unused")
    public EnabledCondition(String name, Supplier<Boolean> condition)
    {
        this.name = new ResourceLocation(ParryingMod.MOD_ID, name);
        this.condition = condition;
    }

    @Override
    public ResourceLocation getID()
    {
        return name;
    }

    @Override
    public boolean test()//eval the condition
    {
        return condition.get();
    }

    //this acts as a go-between for json and minecraft
    public class Serializer implements IConditionSerializer<EnabledCondition>
    {
        @Override
        public void write(JsonObject json, EnabledCondition value)
        {

        }

        @Override
        public EnabledCondition read(JsonObject json)
        {
            return EnabledCondition.this;//return the EnabledCondition object attached to this serializer
        }

        @Override
        public ResourceLocation getID()
        {
            return EnabledCondition.this.name;//return the name field of the EnabledCondition object attached to this serializer
        }
    }
}
