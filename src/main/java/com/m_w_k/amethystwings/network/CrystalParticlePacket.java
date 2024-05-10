package com.m_w_k.amethystwings.network;

import com.m_w_k.amethystwings.capability.WingsCapDataCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrystalParticlePacket extends PacketHandler.AbstractPacket {
    private final int dataID;
    private final int crystalSlot;
    private final int count;

    public static void send(Level level, int dataID, int crystalSlot, int count) {
        PacketHandler.sendToClients(new CrystalParticlePacket(dataID, crystalSlot, count), level.dimension());
    }

    protected CrystalParticlePacket(int dataID, int crystalSlot, int count) {
        this.dataID = dataID;
        this.crystalSlot = crystalSlot;
        this.count = count;
    }

    protected CrystalParticlePacket(FriendlyByteBuf buf) {
        this.dataID = buf.readInt();
        this.crystalSlot = buf.readInt();
        this.count = buf.readInt();
    }

    @Override
    protected void encode(FriendlyByteBuf buf) {
        buf.writeInt(dataID);
        buf.writeInt(crystalSlot);
        buf.writeInt(count);
    }

    @Override
    protected void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientside(this))
        );
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleClientside(CrystalParticlePacket packet) {
        WingsCapDataCache.WingsCapData data = WingsCapDataCache.accessData(packet.dataID);
        if (data != null) {
            data.getData(packet.crystalSlot).particlesToRender += packet.count;
        }
    }
}
