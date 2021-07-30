package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Items.APItem;
import com.theishiopian.parrying.Mechanics.Backstab;
import com.theishiopian.parrying.Mechanics.Deflection;
import com.theishiopian.parrying.Mechanics.Dodging;
import com.theishiopian.parrying.Mechanics.Parrying;
import com.theishiopian.parrying.Registration.ModAttributes;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CommonEvents
{
    static boolean smashing = false;
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
       if(!event.getEntity().level.isClientSide)
       {
           LivingEntity entity = event.getEntityLiving();
           LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
           Parrying.Parry(event);

           //ParryingMod.LOGGER.info(smashing);

           if(attacker != null)
           {
               APItem weapon = attacker.getMainHandItem().getItem() instanceof APItem ? (APItem) attacker.getMainHandItem().getItem() : null;

               if(weapon != null && !smashing)
               {
                    float amount = event.getAmount();
                    //ParryingMod.LOGGER.info(amount);

                    smashing = true;
                    float ap = (float) weapon.getAttributeModifiers(EquipmentSlotType.MAINHAND, attacker.getMainHandItem()).get(ModAttributes.AP.get()).stream().findFirst().get().getAmount();
                    float nonAP = 1 - ap;
                    float dmgAP = amount * ap;
                    float dmgNAP = amount * nonAP;

                    String damageSrc = "bludgeoning.player";

                    entity.hurt(new EntityDamageSource(damageSrc, attacker), dmgNAP);
                    entity.invulnerableTime = 0;
                    entity.hurt(new EntityDamageSource(damageSrc, attacker).bypassArmor(), dmgAP);

                    smashing = false;

                    event.setCanceled(true);
               }
           }
       }
    }

    public static void ArrowParryEvent(ProjectileImpactEvent.Arrow event)
    {
        Deflection.Deflect(event);
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        //ParryingMod.LOGGER.info("Entity " + event.getEntity().getName().getString() + " took " + event.getAmount() + " damage");
        if(event.getEntity() instanceof LivingEntity)
        {
            LivingEntity entity = event.getEntityLiving();
            //LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;

            if((!(entity instanceof PlayerEntity)) && entity.hasEffect(ModEffects.STUNNED.get()))
            {
                event.setAmount(event.getAmount() * 1.5f);
            }

            Backstab.DoBackstab(event, entity);
        }
    }

//    public static void OnTick(TickEvent.PlayerTickEvent event)
//    {
//        float swing = event.player.attackAnim;
//        int swingT = event.player.swingTime;
//
//       if(event.player instanceof ServerPlayerEntity)
//       {
//           ParryingMod.LOGGER.info("Anim: " + swing);
//           ParryingMod.LOGGER.info("Time: " + swingT);
//       }
//    }

    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        Dodging.dodgeCooldown.replaceAll((k, v) -> v - 1);
        Dodging.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}