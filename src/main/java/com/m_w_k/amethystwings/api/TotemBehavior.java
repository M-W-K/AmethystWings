package com.m_w_k.amethystwings.api;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public enum TotemBehavior {
    NORMAL, VENGEFUL, MERCIFUL;

    public static final String IDENTIFIER = "totem";
    public static final String NOURISHING = "hunger_totem";

    public void onSave(LivingEntity entitySaved) {
        if (this == VENGEFUL) {
            double size = 10;
            for (Entity entity : entitySaved.level().getEntities(entitySaved, AABB.ofSize(entitySaved.position(), size, size, size))) {
                if (entity instanceof LivingEntity living && entity.distanceToSqr(entitySaved) < size * size) {
                    living.knockback(1.0, entitySaved.getX() - entity.getX(), entitySaved.getZ() - entity.getZ());
                    living.hurtMarked = true;
                    strengtheningEffect(living, MobEffects.MOVEMENT_SLOWDOWN, 1200);
                    strengtheningEffect(living, MobEffects.WEAKNESS, 2400);
                }
            }

        } else if (this == MERCIFUL) {
            strengtheningEffect(entitySaved, MobEffects.DAMAGE_RESISTANCE, 1200);
            strengtheningEffect(entitySaved, MobEffects.DAMAGE_BOOST, 2400);
        }
    }

    private void strengtheningEffect(LivingEntity living, MobEffect effect, int duration) {
        MobEffectInstance existing = living.getEffect(effect);
        int existingStrength = existing == null ? -1 : existing.getAmplifier();
        living.addEffect(new MobEffectInstance(effect, duration, 1 + existingStrength));
    }
}
