package com.m_w_k.amethystwings.client.renderer;

import com.m_w_k.amethystwings.api.util.WingsAction;
import com.m_w_k.amethystwings.api.util.WingsRenderHelper;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.client.model.WingsModel;
import com.m_w_k.amethystwings.item.WingsItem;
import com.m_w_k.amethystwings.registry.AmethystWingsItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import java.util.Iterator;
import java.util.List;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

@OnlyIn(Dist.CLIENT)
public class WingsItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public static final WingsModel WINGS_MODEL = new WingsModel(WingsModel.createLayer().bakeRoot());
    public static final ModelResourceLocation WINGS_INVENTORY_MODEL = new ModelResourceLocation(MODID, "wings_controller_inventory", "inventory");
    public static final ModelResourceLocation PRIMITIVE_WINGS_INVENTORY_MODEL = new ModelResourceLocation(MODID, "primitive_wings_controller_inventory", "inventory");
    public static final ResourceLocation WINGS_TEXTURE = new ResourceLocation(MODID, "textures/block/wings_controller.png");
    public static final ResourceLocation PRIMITIVE_WINGS_TEXTURE = new ResourceLocation(MODID, "textures/block/primitive_wings_controller.png");

    private final static PoseStack ELYTRA_HELPER = new PoseStack();
    private final static ModelPart RIGHT_FAKE_WING = new ModelPart(null, null);
    private final static ModelPart LEFT_FAKE_WING = new ModelPart(null, null);

    private final static Quaternionf CORRECTION = new Quaternionf(0, 0, 1, 0);

    public WingsItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (context == ItemDisplayContext.GROUND || context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIXED) {
            poseStack.popPose(); // remove the -0.5, -0.5, -0.5 shift
            poseStack.pushPose();
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            ResourceLocation modelLoc = stack.getItem() == AmethystWingsItemsRegistry.WINGS.get() ? WINGS_INVENTORY_MODEL : PRIMITIVE_WINGS_INVENTORY_MODEL;
            BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(modelLoc);
            renderer.render(stack, context, false, poseStack, buffer, combinedLightIn, combinedOverlayIn, model);
        } else {
            boolean mirrored = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            ResourceLocation tex = stack.getItem() == AmethystWingsItemsRegistry.WINGS.get() ? WINGS_TEXTURE : PRIMITIVE_WINGS_TEXTURE;
            WINGS_MODEL.render(poseStack, buffer.getBuffer(RenderType.entitySolid(tex)), combinedLightIn, combinedOverlayIn, mirrored);
            if (context.firstPerson() && stack.getItem() instanceof WingsItem item) {
                crystalFirstPersonRender(item.getCapability(stack), poseStack, buffer, combinedLightIn, combinedOverlayIn, context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            }
        }
    }

    private static void crystalFirstPersonRender(@NotNull WingsCapability cap, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn, boolean mirrored) {
        if (!cap.isBlocking()) return;
        assert Minecraft.getInstance().player != null;
        cap.handleParticles(Minecraft.getInstance().player);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(mirrored ? 0.6 : -0.52, 0.2, 0.55);
        poseStack.mulPose(CORRECTION);
        Iterator<WingsRenderHelper.CrystalTarget>  iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.SHIELD);
        for (WingsCapability.Crystal crystal : cap.getCrystalsShieldSorted().fullList) {
            if (!iter.hasNext()) return;
            WingsRenderHelper.CrystalTarget target = iter.next();
            poseStack.pushPose();

            Vec3 offset = target.targetPosition();
            poseStack.translate(offset.x(), offset.y(), offset.z());
            poseStack.pushPose();

            poseStack.mulPose(target.targetRotation().get(new Quaternionf()));
            renderCrystal(cap, crystal, poseStack, buffer, combinedLightIn, combinedOverlayIn);

            poseStack.popPose();
            poseStack.popPose();
        }
    }

    // called externally
    public static void crystalRender(@NotNull WingsCapability cap, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, LivingEntity entity, int combinedLightIn, int combinedOverlayIn) {
        float partialTicks = Minecraft.getInstance().getPartialTick();
        cap.prepareForRender(partialTicks, entity);
        setupAnim(cap, entity);
        PoseStack.Pose poseTop = poseStack.last();
        poseStack.popPose();
        PoseStack.Pose poseLER = poseStack.last();
        // find the matrix that goes from the LivingEntityRenderer pose to the top pose via inversion.
        Matrix4f matrixLER = poseLER.pose().invert(new Matrix4f());
        matrixLER.mul(poseTop.pose());
        Transformation transformationLER = new Transformation(matrixLER);

        for (WingsCapability.Crystal crystal : cap.getCrystals()) {
            poseStack.pushPose();
            // absolute offset so that position lerping works on entity rotation
            Vec3 offset = crystal.calculateOffset(matrixLER); // TODO fix crouch lerping
            poseStack.translate(offset.x(), offset.y(), offset.z());
            // restore the top pose
            poseStack.pushTransformation(transformationLER);
            poseStack.pushPose();

            Quaterniond rot = crystal.calculateRotation(matrixLER); // TODO fix rot lerping
            poseStack.mulPose(rot.get(new Quaternionf()));

            renderCrystal(cap, crystal, poseStack, buffer, combinedLightIn, combinedOverlayIn);

            poseStack.popPose();
            poseStack.popPose();
            poseStack.popPose();
        }
        // restore the top pose
        poseStack.pushTransformation(transformationLER);
    }

    private static void renderCrystal(WingsCapability cap, @NotNull WingsCapability.Crystal crystal, @NotNull PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (cap.stack == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(crystal.crystalItem.getWingsModelLoc());
        renderer.render(cap.stack, ItemDisplayContext.NONE, false, poseStack, buffer, combinedLightIn, combinedOverlayIn, model);
        poseStack.popPose();
    }

    /**
     * Ripped directly from {@link net.minecraft.client.model.ElytraModel}
     */
    private static void setupAnim(WingsCapability cap, LivingEntity entity) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entity.isFallFlying()) {
            float f4 = 1.0F;
            Vec3 vec3 = entity.getDeltaMovement();
            if (vec3.y < 0.0D) {
                Vec3 vec31 = vec3.normalize();
                f4 = 1.0F - (float)Math.pow(-vec31.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (entity.isCrouching()) {
            f = 0.6981317F;
            f1 = (-(float)Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        LEFT_FAKE_WING.y = f2;
        if (entity instanceof AbstractClientPlayer abstractclientplayer) {
            abstractclientplayer.elytraRotX += (f - abstractclientplayer.elytraRotX) * 0.1F;
            abstractclientplayer.elytraRotY += (f3 - abstractclientplayer.elytraRotY) * 0.1F;
            abstractclientplayer.elytraRotZ += (f1 - abstractclientplayer.elytraRotZ) * 0.1F;
            LEFT_FAKE_WING.xRot = abstractclientplayer.elytraRotX;
            LEFT_FAKE_WING.yRot = abstractclientplayer.elytraRotY;
            LEFT_FAKE_WING.zRot = abstractclientplayer.elytraRotZ;
        } else {
            LEFT_FAKE_WING.xRot = f;
            LEFT_FAKE_WING.zRot = f1;
            LEFT_FAKE_WING.yRot = f3;
        }

        RIGHT_FAKE_WING.yRot = -LEFT_FAKE_WING.yRot;
        RIGHT_FAKE_WING.y = LEFT_FAKE_WING.y;
        RIGHT_FAKE_WING.xRot = LEFT_FAKE_WING.xRot;
        RIGHT_FAKE_WING.zRot = -LEFT_FAKE_WING.zRot;

        ELYTRA_HELPER.pushPose();
        RIGHT_FAKE_WING.translateAndRotate(ELYTRA_HELPER);
        Matrix4f temp = ELYTRA_HELPER.last().pose();
        ELYTRA_HELPER.popPose();
        ELYTRA_HELPER.pushPose();
        LEFT_FAKE_WING.translateAndRotate(ELYTRA_HELPER);
        cap.setElytraRenderMatrices(ELYTRA_HELPER.last().pose(), temp);
        ELYTRA_HELPER.popPose();
    }

    public static List<ResourceLocation> getWingsInventoryModels() {
        return List.of(WINGS_INVENTORY_MODEL, PRIMITIVE_WINGS_INVENTORY_MODEL);
    }
}
