package com.m_w_k.amethystwings.client.renderer;

import com.m_w_k.amethystwings.client.model.WingsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class WingsItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public static final WingsModel WINGS_MODEL = new WingsModel(WingsModel.createLayer().bakeRoot());
    public static final ResourceLocation WINGS_TEXTURE = new ResourceLocation(MODID, "textures/entity/wings_controller.png");

    public WingsItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        boolean mirrored = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(WINGS_TEXTURE));
        WINGS_MODEL.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn, mirrored);
        poseStack.popPose();
    }
}
