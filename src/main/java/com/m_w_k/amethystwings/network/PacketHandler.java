package com.m_w_k.amethystwings.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public abstract class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("mymodid", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id;

    public static void init() {
        INSTANCE.registerMessage(0, WingsBoostPacket.class,
                WingsBoostPacket::encode, WingsBoostPacket::new, WingsBoostPacket::handle);
    }

    protected static void sendToServer(AbstractPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    protected abstract static class AbstractPacket {
        protected abstract void encode(FriendlyByteBuf buf);
        protected abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);
    }
}
