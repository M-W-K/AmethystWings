package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.network.WingsBoostPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements WingsBoostPacket.AmethystWingsSGPLIMixin {
    @Shadow private int aboveGroundTickCount;

    @Override
    public void amethystWings$resetTimeFloating() {
        this.aboveGroundTickCount = 0;
    }
}
