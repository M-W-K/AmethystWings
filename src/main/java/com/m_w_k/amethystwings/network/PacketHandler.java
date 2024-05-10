package com.m_w_k.amethystwings.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
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
        INSTANCE.registerMessage(id++, WingsBoostPacket.class,
                WingsBoostPacket::encode, WingsBoostPacket::new, WingsBoostPacket::handle);
        INSTANCE.registerMessage(id++, CrystalParticlePacket.class,
                CrystalParticlePacket::encode, CrystalParticlePacket::new, CrystalParticlePacket::handle);
    }

    protected static void sendToServer(AbstractPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    protected static void sendToClients(AbstractPacket packet, ResourceKey<Level> dimension) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
    }

    protected abstract static class AbstractPacket {
        protected abstract void encode(FriendlyByteBuf buf);
        protected abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);
    }
}
