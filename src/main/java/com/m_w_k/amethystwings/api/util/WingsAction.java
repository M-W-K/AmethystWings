package com.m_w_k.amethystwings.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public enum WingsAction {
    // useful keys
    SHIELD, ELYTRA, BOOST, NONE,
    // keys used for render logic
    IDLE, SHIELD_IDLE;

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isElytraAttached() {
        return this == ELYTRA || this == BOOST || this == IDLE;
    }

    public void appendHoverText(List<Component> components) {
        components.add(Component.translatable("item.amethystwings.wings_controller.action." + this.name().toLowerCase()).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
    }
}
