package com.m_w_k.amethystwings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
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

    public static final NonNullLazy<KeyMapping> BOOST = NonNullLazy.of(() -> new KeyMapping("key.amethystwings.boost.description", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, "key.amethystwings.category"));

    public static void registerBindings(@NotNull RegisterKeyMappingsEvent event) {
        event.register(Keybindings.BOOST.get());
    }
}
