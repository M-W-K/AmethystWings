package com.m_w_k.amethystwings.client.renderer;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.client.model.WingsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class WingsItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public static final WingsModel WINGS_MODEL = new WingsModel(WingsModel.createLayer().bakeRoot());
    public static final ModelResourceLocation WINGS_INVENTORY_MODEL = new ModelResourceLocation(MODID, "wings_controller_inventory", "inventory");
    public static final ResourceLocation WINGS_TEXTURE = new ResourceLocation(MODID, "textures/entity/wings_controller.png");

    public WingsItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        poseStack.popPose(); // remove the -0.5, -0.5, -0.5 shift
        poseStack.pushPose();
        if (context == ItemDisplayContext.GROUND || context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIXED) {
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(WINGS_INVENTORY_MODEL);
            renderer.render(stack, context, false, poseStack, buffer, combinedLightIn, combinedOverlayIn, model);
            return;
        }
        handRender(context, poseStack, buffer, combinedLightIn, combinedOverlayIn);
    }

    private static void handRender(@NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, -0.5);
        boolean mirrored = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        WINGS_MODEL.render(poseStack, buffer.getBuffer(RenderType.entitySolid(WINGS_TEXTURE)), combinedLightIn, combinedOverlayIn, mirrored);
        poseStack.popPose();
    }

    // called externally
    public static void crystalRender(@NotNull WingsCapability cap, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, LivingEntity entity, int combinedLightIn, int combinedOverlayIn) {
        float partialTicks = Minecraft.getInstance().getPartialTick();
        cap.prepareForRender(partialTicks, entity);
        PoseStack.Pose livingEntityRendererPose = poseStack.last();
        poseStack.popPose();
        // find the matrix that goes from the current pose to the livingEntityRenderer pose via inversion.
        Matrix4f matrix = poseStack.last().pose().invert(new Matrix4f());
        matrix.mul(livingEntityRendererPose.pose());
        Transformation transformation = new Transformation(matrix);

        for (WingsCapability.Crystal crystal : cap.getCrystals()) {

            // absolute offset so that position lerping works on entity rotation
            Vec3 offset = crystal.calculateOffset(matrix); // TODO crouch is weird

            poseStack.pushPose();
            poseStack.translate(offset.x(), offset.y(), offset.z());
            poseStack.pushTransformation(transformation); // reintroduce the LivingEntityRenderer's effects
            poseStack.pushPose();

            Quaterniond rot = crystal.calculateRotation(matrix);
            poseStack.mulPose(rot.get(new Quaternionf()));

            crystal.render(poseStack, buffer, combinedLightIn, combinedOverlayIn);

            poseStack.popPose();
            poseStack.popPose();
            poseStack.popPose();
        }
        poseStack.pushTransformation(transformation); // restore the LivingEntityRenderer's pose
    }
}
