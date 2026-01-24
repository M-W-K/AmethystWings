package com.m_w_k.amethystwings.network;

import com.m_w_k.amethystwings.inventory.WingsContainer;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WingsContainerOpenPacket extends PacketHandler.AbstractPacket {

    private final int slot;

    public static void send(int slot) {
        PacketHandler.sendToServer(new WingsContainerOpenPacket(slot));
    }

    protected WingsContainerOpenPacket(int slot) {
        this.slot = slot;
    }

    protected WingsContainerOpenPacket(FriendlyByteBuf buf) {
        slot = buf.readVarInt();
    }

    @Override
    protected void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(slot);
    }

    @Override
    protected void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null && sender.getInventory().getItem(slot).getItem() instanceof WingsItem) {
                WingsContainer.openGUI(sender, slot);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
