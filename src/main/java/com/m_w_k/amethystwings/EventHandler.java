package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public final class EventHandler {

    private static final List<Runnable> COOLDOWNED_WINGS = new ObjectArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.isCanceled()) return;
        LivingEntity defender = event.getEntity();
        ItemStack stack = defender.getUseItem();
        if (stack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(stack);
            boolean shatter = false;
            Runnable knockback = () -> doKnockback(defender, new Vec3(-defender.getLookAngle().x(), 8, -defender.getLookAngle().y()));
            if (!event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE)) {
                Entity entity = event.getDamageSource().getDirectEntity();
                if (entity instanceof LivingEntity attacker) {
                    shatter = attacker.getMainHandItem().canDisableShield(defender.getUseItem(), defender, attacker);
                    knockback = () -> doKnockback(defender, new Vec3(attacker.getX() - defender.getX(), 8, attacker.getZ() - defender.getZ()));
                }
            }
            cap.takeBlockDamage(defender,
                    event.getBlockedDamage(), shatter, knockback);
            if (shatter && !AmethystWingsConfig.shieldBreakCooldown) COOLDOWNED_WINGS.add(() -> ((Player) defender).getCooldowns().removeCooldown(item));
        }
    }

    private static void doKnockback(LivingEntity entity, Vec3 vec) {
        entity.knockback(vec.y(), vec.x(), vec.z());
        entity.hurtMarked = true;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (COOLDOWNED_WINGS.size() > 0) {
            for (Runnable runnable : COOLDOWNED_WINGS) runnable.run();
            COOLDOWNED_WINGS.clear();
        }
    }
}
