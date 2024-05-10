package com.m_w_k.amethystwings.network;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WingsBoostPacket extends PacketHandler.AbstractPacket {

    private final boolean mainHand;

    public static void send(boolean mainHand) {
        PacketHandler.sendToServer(new WingsBoostPacket(mainHand));
    }

    protected WingsBoostPacket(boolean mainHand) {
        this.mainHand = mainHand;
    }

    protected WingsBoostPacket(FriendlyByteBuf buf) {
        mainHand = buf.readBoolean();
    }

    @Override
    protected void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(mainHand);
    }

    @Override
    protected void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            ItemStack wingStack;
            if (mainHand) {
                wingStack = sender.getMainHandItem();
            } else wingStack = sender.getOffhandItem();
            if (wingStack.getItem() instanceof WingsItem item) {
                WingsCapability cap = item.getCapability(wingStack);
                cap.doBoost(sender, sender.isFallFlying(), mainHand);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
