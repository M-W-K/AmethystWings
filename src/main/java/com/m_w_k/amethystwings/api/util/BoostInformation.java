package com.m_w_k.amethystwings.api.util;

import com.m_w_k.amethystwings.EventHandler;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record BoostInformation(ServerPlayer player, double yHeightOfJump) {

    public static void handle(@NotNull ServerPlayer player) {
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            EventHandler.registerFlyingPlayer(new BoostInformation(player, player.getY()));
        }
    }
}
