package com.m_w_k.amethystwings.inventory;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.gui.menu.WingsMenu;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WingsContainer implements MenuProvider, Nameable, Container {

    protected final WingsCapability capability;
    protected final ItemStack selfStack;
    protected final InteractionHand hand;

    public WingsContainer(ItemStack wingsStack, Player player, InteractionHand hand) {
        this.selfStack = wingsStack;
        this.hand = hand;
        this.capability = this.selfStack.getCapability(WingsCapability.WINGS_CAPABILITY)
                .orElse(WingsCapability.EMPTY);
    }

    public static void openGUI(ServerPlayer player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            NetworkHooks.openScreen(player, new WingsContainer(player.getItemInHand(hand), player, hand));
        }
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
        return new WingsMenu(containerID, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 54;
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
        return player.getItemInHand(this.hand) == this.selfStack;
    }

    @Override
    public void clearContent() {
        this.capability.clear();
    }
}
