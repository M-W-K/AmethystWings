package com.m_w_k.amethystwings.client.model;

import com.google.common.collect.ImmutableList;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

import java.util.List;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class CrystalModel {
    public static final List<ResourceLocation> DEFAULT_TEXTURES = ImmutableList.of(
            new ResourceLocation(MODID, "textures/entity/resonant_crystal.png"),
            new ResourceLocation(MODID, "textures/entity/hardened_crystal.png"),
            new ResourceLocation(MODID, "textures/entity/energetic_crystal.png"),
            new ResourceLocation(MODID, "textures/entity/shaped_crystal.png"),
            new ResourceLocation(MODID, "textures/entity/auric_crystal.png")
    );

    private final ModelPart small_crystal;
    private final ModelPart medium_crystal;
    private final ModelPart large_crystal;

    public CrystalModel(ModelPart root) {
        this.small_crystal = root.getChild("small_crystal");
        this.medium_crystal = root.getChild("medium_crystal");
        this.large_crystal = root.getChild("large_crystal");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("small_crystal", CubeListBuilder.create()
                .texOffs(rand(12), rand(14))
                .addBox(-1, 1, -2, 1, 1, 1), PartPose.offset(0.5f, 0, 0));
        root.addOrReplaceChild("medium_crystal", CubeListBuilder.create()
                .texOffs(rand(12), rand(11))
                .addBox(-1, -3, 1, 1, 4, 1), PartPose.offset(0.5f, 0, 0));
        root.addOrReplaceChild("large_crystal", CubeListBuilder.create()
                .texOffs(rand(10), rand(9))
                .addBox(-1, -2, -1, 1, 5, 2), PartPose.offset(0.5f, 0, 0));

        return LayerDefinition.create(mesh, 16, 16);
    }

    private static int rand(int max) {
        return (int) Math.round(Math.random() * max);

    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int combinedLightIn, int combinedOverlayIn, boolean mirrored) {
        poseStack.pushPose();
        if (mirrored)
            poseStack.rotateAround(new Quaternionf(new AxisAngle4d(Math.toRadians(180), 0, 1, 0)), 0, 0, 0);
        this.small_crystal.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        this.medium_crystal.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        this.large_crystal.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }
}
