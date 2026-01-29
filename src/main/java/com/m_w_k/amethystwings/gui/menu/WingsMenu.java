package com.m_w_k.amethystwings.gui.menu;

import com.m_w_k.amethystwings.inventory.WingsContainer;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import com.m_w_k.amethystwings.registry.AmethystWingsGUIRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class WingsMenu extends AbstractContainerMenu {
    private final Container container;
    public final int lockedslot;

    private final Player player;

    public WingsMenu(int p_39230_, Inventory p_39231_, WingsContainer wings, int lockedslot) {
        super(AmethystWingsGUIRegistry.WINGS_MENU.get(), p_39230_);
        this.container = wings;
        this.player = p_39231_.player;
        wings.startOpen(p_39231_.player);
        int i = (wings.getRowCount() - 4) * 18;
        this.lockedslot = lockedslot;

        for(int j = 0; j < wings.getRowCount(); ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new CrystalSlot(wings, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(p_39231_, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i);
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(p_39231_, i1, 8 + i1 * 18, 161 + i);
        }

        // if the slot containing the stack is not in the menu, nbt sync can be lost
//        if (this.lockedslot >= 36) this.addSlot(p_39231_, this.lockedslot, -20, -20);
    }

    public static WingsMenu fromNetwork(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
        int slot = buf.readVarInt();
        WingsContainer container = new WingsContainer(playerInventory.player, slot);
        return new WingsMenu(windowId, playerInventory, container, slot);
    }

    protected void addSlot(Container p_40223_, int slotID, int p_40225_, int p_40226_) {
        if (slotID == lockedslot) {
            this.addSlot(new DisabledSlot(p_40223_, slotID, p_40225_, p_40226_));
        } else {
            this.addSlot(new Slot(p_40223_, slotID, p_40225_, p_40226_));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player p_39253_, int p_39254_) {
        ItemStack itemstack = ItemStack.EMPTY;
        if(p_39254_ == lockedslot) return itemstack;
        Slot slot = this.slots.get(p_39254_);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_39254_ < container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void removed(@NotNull Player p_39251_) {
        super.removed(p_39251_);
        this.container.stopOpen(p_39251_);
    }

    @Override
    public void suppressRemoteUpdates() {
        super.suppressRemoteUpdates();
        this.player.inventoryMenu.suppressRemoteUpdates();
    }

    @Override
    public void resumeRemoteUpdates() {
        super.resumeRemoteUpdates();
        this.player.inventoryMenu.resumeRemoteUpdates();
    }

    @Override
    public boolean stillValid(@NotNull Player p_38874_) {
        return this.container.stillValid(p_38874_);
    }

    public @Range(from = 1, to = 6) int getRowCount() {
        return ((WingsContainer) container).getRowCount();
    }

    protected static class CrystalSlot extends Slot {

        public CrystalSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
            super(p_40223_, p_40224_, p_40225_, p_40226_);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof WingsCrystalItem;
        }
    }

    protected static class DisabledSlot extends Slot {

        public DisabledSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
            super(p_40223_, p_40224_, p_40225_, p_40226_);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack p_40231_) {
            return false;
        }

        @Override
        public boolean mayPickup(@NotNull Player p_40228_) {
            return false;
        }

        @Override
        public boolean isHighlightable() {
            return false;
        }
    }
}
