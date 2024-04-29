package net.minecraft.world.entity;

public class LivingEntityAccessor {
    public static void blockedByShield(LivingEntity attacker, LivingEntity defender) {
        attacker.blockedByShield(defender);
    }
}
