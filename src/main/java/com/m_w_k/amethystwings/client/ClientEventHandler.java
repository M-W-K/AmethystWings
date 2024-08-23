package com.m_w_k.amethystwings.client;

import com.m_w_k.amethystwings.AmethystWingsMod;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AmethystWingsMod.MODID)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.@NotNull ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (Keybindings.BOOST.get().consumeClick()) {
                if (!tryBoost(EquipmentSlot.MAINHAND))
                    tryBoost(EquipmentSlot.OFFHAND);
            }
        }
    }

    private static boolean tryBoost(EquipmentSlot slot) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        if (player.isUnderWater()) return true;
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(stack);
            if (cap.canBoost()) {
                cap.doBoost(player, player.isFallFlying(), slot == EquipmentSlot.MAINHAND);
                return true;
            }
        }
        return false;
    }
}
