package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import com.m_w_k.amethystwings.registry.AmethystWingsAttributeRegistry;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AmethystWingsMod.MODID)
public final class EventHandler {

    private static final List<Runnable> COOLDOWNED_WINGS = new ObjectArrayList<>();
    private static final Object2DoubleOpenHashMap<UUID> BOOSTED_ENTITIES = new Object2DoubleOpenHashMap<>();
    private static final Object2IntOpenHashMap<UUID> BOOSTED_ENTITIES_DECAY_TIMES = new Object2IntOpenHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onShieldBlock(@NotNull ShieldBlockEvent event) {
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

    private static void doKnockback(@NotNull LivingEntity entity, @NotNull Vec3 vec) {
        entity.knockback(vec.y(), vec.x(), vec.z());
        entity.hurtMarked = true;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (!COOLDOWNED_WINGS.isEmpty()) {
            for (Runnable runnable : COOLDOWNED_WINGS) runnable.run();
            COOLDOWNED_WINGS.clear();
        }
        if (event.getServer().getTickCount() % 617 == 0) {
            int tick = event.getServer().getTickCount();
            var iter = BOOSTED_ENTITIES_DECAY_TIMES.object2IntEntrySet().fastIterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                if (entry.getIntValue() < tick) iter.remove();
            }
        }

    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (BOOSTED_ENTITIES.isEmpty() || event.getEntity().level().isClientSide()) return;
        UUID uuid = event.getEntity().getUUID();
        if (BOOSTED_ENTITIES.containsKey(uuid)) {
            double actualFall = BOOSTED_ENTITIES.getDouble(uuid) - event.getEntity().getY();
            if (actualFall <= 0) event.setCanceled(true);
            if (actualFall < event.getDistance()) event.setDistance((float) actualFall);
            BOOSTED_ENTITIES.removeDouble(uuid);
            BOOSTED_ENTITIES_DECAY_TIMES.removeInt(uuid);
        }
    }

    public static void markBoosted(@NotNull LivingEntity entity, double bonus) {
        if (entity.getServer() == null) return;
        BOOSTED_ENTITIES.put(entity.getUUID(), entity.getY());
        BOOSTED_ENTITIES_DECAY_TIMES.put(entity.getUUID(), entity.getServer().getTickCount() + computeDecayTime(bonus));
    }

    private static int computeDecayTime(double bonus) {
        return (int) (AmethystWingsConfig.boostStrength * AmethystWingsConfig.boostStrength * bonus * bonus * 600);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void applyAttributesToDamage(@NotNull LivingHurtEvent event) {
        double amount = event.getAmount();
        if (event.isCanceled() || amount <= 1) return;
        LivingEntity receiver = event.getEntity();
        // apply warding
        // block things that armor can block, and magic-type damage that witches can resist.
        if (!event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            double reduction = receiver.getAttributeValue(AmethystWingsAttributeRegistry.WARDING.get());
            amount = Math.max(amount - reduction, 1);
        }
        // apply barrier
        // blocks everything that doesn't bypass invulnerability
        if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            double barrier = receiver.getAttributeValue(AmethystWingsAttributeRegistry.BARRIER.get());
            amount = Math.pow(amount, 1 - barrier);
        }
        event.setAmount((float) amount);
    }
}
