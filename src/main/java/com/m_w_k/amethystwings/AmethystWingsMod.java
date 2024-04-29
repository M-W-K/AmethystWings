package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import com.m_w_k.amethystwings.registry.AmethystWingsRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LivingEntityAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Consumer;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AmethystWingsMod.MODID)
public class AmethystWingsMod {
    public static final String MODID = "assets/amethystwings";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmethystWingsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AmethystWingsRegistry.init(modEventBus);

        // allow cancellation for other reasons to go through first
        modEventBus.addListener(EventPriority.LOW, AmethystWingsMod::onShieldBlock);

//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        LivingEntity defender = event.getEntity();
        ItemStack stack = defender.getUseItem();
        if (stack.getItem() instanceof WingsItem) {
            var lazycap = stack.getCapability(WingsCapability.WINGS_CAPABILITY);
            if (lazycap.isPresent()) {
                WingsCapability cap = lazycap.orElse(WingsCapability.EMPTY);
                event.setCanceled(true);
                boolean shatter = false;
                Runnable knockback = () -> defender.knockback(5d, defender.getLookAngle().x(), defender.getLookAngle().y());
                if (!event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE)) {
                    Entity entity = event.getDamageSource().getDirectEntity();
                    if (entity instanceof LivingEntity attacker) {
                        LivingEntityAccessor.blockedByShield(attacker, defender);
                        shatter = attacker.getMainHandItem().canDisableShield(defender.getUseItem(), defender, attacker);
                        knockback = () -> defender.knockback(5d, defender.getX() - attacker.getX(), defender.getZ() - attacker.getZ());
                    }
                }
                cap.takeBlockDamage(event.getBlockedDamage(), shatter, knockback);
            }
        }
    }
}
