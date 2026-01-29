package com.m_w_k.amethystwings.inventory;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.gui.menu.WingsMenu;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class WingsContainer implements MenuProvider, Nameable, Container {

    protected WingsCapability capability;
    protected ItemStack selfStack;
    protected final int stackSlot;

    public WingsContainer(Player player, int stackSlot) {
        this.selfStack = player.getInventory().getItem(stackSlot);
        this.stackSlot = stackSlot;
        this.capability = this.selfStack.getCapability(WingsCapability.WINGS_CAPABILITY)
                .orElse(WingsCapability.EMPTY);
    }

    public static void openGUI(ServerPlayer player, int stackSlot) {
        NetworkHooks.openScreen(player, new WingsContainer(player, stackSlot), buf -> buf.writeVarInt(stackSlot));
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("screen.amethystwings.item");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("screen.amethystwings.item");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inventory, @NotNull Player player) {
        return new WingsMenu(containerID, inventory, this, stackSlot);
    }

    @Override
    public int getContainerSize() {
        return getRowCount() * 9;
    }

    public @Range(from = 1, to = 6) int getRowCount() {
        return ((WingsItem) selfStack.getItem()).getRowCount();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.capability.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.capability.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        return this.capability.extractItem(slot, count, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.capability.getStackInSlot(slot);
        this.capability.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.capability.setStackInSlot(slot, stack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (capability == WingsCapability.EMPTY) return false;
        ItemStack stack = player.getInventory().getItem(stackSlot);
        if (stack == this.selfStack) return true;
        WingsCapability cap = stack.getCapability(WingsCapability.WINGS_CAPABILITY).orElse(WingsCapability.EMPTY);
        if (cap == WingsCapability.EMPTY) return false;
        if (cap.getDataID() == capability.getDataID()) {
            selfStack = stack;
            capability = cap;
            return true;
        }
        return false;
    }

    @Override
    public void clearContent() {
        this.capability.clear();
    }
}
