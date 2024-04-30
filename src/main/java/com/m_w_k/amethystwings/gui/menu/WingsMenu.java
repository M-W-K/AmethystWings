package com.m_w_k.amethystwings.gui.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WingsMenu extends AbstractContainerMenu {
    private final Container container;
    private final int lockedslot;

    public WingsMenu(int p_39230_, Inventory p_39231_, Container p_39232_) {
        super(MenuType.GENERIC_9x6, p_39230_);
        this.container = p_39232_;
        p_39232_.startOpen(p_39231_.player);
        int i = 36;
        lockedslot = p_39231_.selected;

        for(int j = 0; j < 6; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(p_39232_, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(p_39231_, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            if (i1 == lockedslot)
                this.addSlot(new DisabledSlot(p_39231_, i1, 8 + i1 * 18, 161 + i));
            else
                this.addSlot(new Slot(p_39231_, i1, 8 + i1 * 18, 161 + i));
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
            if (p_39254_ < 54) {
                if (!this.moveItemStackTo(itemstack1, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 54, false)) {
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
    public boolean stillValid(@NotNull Player p_38874_) {
        return this.container.stillValid(p_38874_);
    }

    protected static class DisabledSlot extends Slot {

        public DisabledSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
            super(p_40223_, p_40224_, p_40225_, p_40226_);
        }

        @Override
        public boolean mayPickup(@NotNull Player p_40228_) {
            return false;
        }
    }
}
