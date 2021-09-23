package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Entity.SpearEntity;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class is used to register custom entities.
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ParryingMod.MOD_ID);

    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = ENTITY_TYPES.register("spear", () -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build(new ResourceLocation(ParryingMod.MOD_ID, "spear").toString()));
}
