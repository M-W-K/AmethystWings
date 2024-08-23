package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.api.util.BoostInformation;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.client.Keybindings;
import com.m_w_k.amethystwings.item.WingsItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = AmethystWingsMod.MODID)
public final class EventHandler {

    private static final List<Runnable> COOLDOWNED_WINGS = new ObjectArrayList<>();
    private static final List<BoostInformation> FLYING_PLAYERS = new ObjectArrayList<>();

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
        if (!FLYING_PLAYERS.isEmpty()) {
            Iterator<BoostInformation> iter = FLYING_PLAYERS.iterator();
            while (iter.hasNext()) {
                BoostInformation next = iter.next();
                if (next.player().getY() < next.yHeightOfJump()) {
                    next.player().getAbilities().mayfly = false;
                    next.player().fallDistance = 0;
                    iter.remove();
                } else if (next.player().onGround()) {
                    next.player().getAbilities().mayfly = false;
                    iter.remove();
                }
            }
        }
    }

    public static void registerFlyingPlayer(BoostInformation information) {
        FLYING_PLAYERS.add(information);
    }
}
