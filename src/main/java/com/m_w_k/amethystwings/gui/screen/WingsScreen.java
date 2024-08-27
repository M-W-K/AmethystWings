package com.m_w_k.amethystwings.gui.screen;

import com.m_w_k.amethystwings.gui.menu.WingsMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WingsScreen extends AbstractContainerScreen<WingsMenu> implements MenuAccess<WingsMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public WingsScreen(WingsMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.imageHeight = 114 + 6 * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void slotClicked(@NotNull Slot p_97778_, int p_97779_, int p_97780_, @NotNull ClickType p_97781_) {
        if (p_97781_ == ClickType.SWAP && (p_97780_ == getMenu().lockedslot || p_97779_ == getMenu().lockedslot)) return;
        super.slotClicked(p_97778_, p_97779_, p_97780_, p_97781_);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float p_97788_, int p_97789_, int p_97790_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, 6 * 18 + 17);
        graphics.blit(CONTAINER_BACKGROUND, i, j + 6 * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
