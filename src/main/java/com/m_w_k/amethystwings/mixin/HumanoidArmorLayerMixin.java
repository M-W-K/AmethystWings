package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.client.renderer.WingsItemStackRenderer;
import com.m_w_k.amethystwings.item.WingsItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "HEAD"))
    private void crystalRender(PoseStack poseStack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int p_117123_, A p_117124_, CallbackInfo ci) {
        if (slot == EquipmentSlot.CHEST) {
            ItemStack stackOffhand = entity.getOffhandItem();
            if (stackOffhand.getItem() instanceof WingsItem item) {
                WingsItemStackRenderer.crystalRender(item.getCapability(stackOffhand), poseStack, buffer, entity, p_117123_, NO_OVERLAY);
                return;
            }
            ItemStack stackMainhand = entity.getMainHandItem();
            if (stackMainhand.getItem() instanceof WingsItem item)
                WingsItemStackRenderer.crystalRender(item.getCapability(stackMainhand), poseStack, buffer, entity, p_117123_, NO_OVERLAY);
        }
    }
}
