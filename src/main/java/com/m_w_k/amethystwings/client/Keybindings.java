package com.m_w_k.amethystwings.client;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.util.NonNullLazy;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public final class Keybindings {

    public static final NonNullLazy<KeyMapping> BOOST = NonNullLazy.of(() -> new ShadowingKeyMapping("key.amethystwings.boost.description", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, "key.amethystwings.category", Minecraft.getInstance().options.keyJump, Keybindings::shouldBoostShadow));

    public static void registerBindings(@NotNull RegisterKeyMappingsEvent event) {
        event.register(Keybindings.BOOST.get());
    }

    private static boolean shouldBoostShadow() {
        if (BOOST.get().same(Minecraft.getInstance().options.keyJump)) {
            LocalPlayer player = Minecraft.getInstance().player;
            return player != null && !canBoost(player.getMainHandItem()) && !canBoost(player.getOffhandItem());
        }
        return false;
    }

    private static boolean canBoost(ItemStack stack) {
        if (stack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(stack);
            return cap.canBoost();
        }
        return false;
    }
}
