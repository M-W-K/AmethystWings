package com.m_w_k.amethystwings.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.function.Function;

public class WingsModel {
    private final ModelPart root;
    private final ModelPart front_brace;
    private final ModelPart back_brace;
    private final ModelPart connector;
    private final ModelPart connector_top;
    private final ModelPart connector_bottom;

    public WingsModel(ModelPart root) {
        this.root = root;
        this.front_brace = root.getChild("front_brace");
        this.back_brace = root.getChild("back_brace");
        this.connector = root.getChild("connector");
        this.connector_top = root.getChild("connector_top");
        this.connector_bottom = root.getChild("connector_bottom");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("front_brace", CubeListBuilder.create().texOffs(0, 10).addBox(5, 3, 9, 6, 6, 2), PartPose.offset(0, 0, 0));
        root.addOrReplaceChild("back_brace", CubeListBuilder.create().texOffs(16, 0).addBox(5, 3, 15, 6, 6, 2), PartPose.offset(0, 0, 0));
        root.addOrReplaceChild("connector", CubeListBuilder.create().texOffs(0, 0).addBox(10, 4, 10, 2, 4, 6), PartPose.offset(0, 0, 0));
        root.addOrReplaceChild("connector_top", CubeListBuilder.create().texOffs(0, 18).addBox(-2, -1, -3, 2, 1, 6), PartPose.offsetAndRotation(12, 8, 13, 0, 0, (float) Math.toRadians(-22.5))); //
        root.addOrReplaceChild("connector_bottom", CubeListBuilder.create().texOffs(10, 12).addBox(-2, 0, -3, 2, 1, 6), PartPose.offsetAndRotation(12, 4, 13, 0, 0, (float) Math.toRadians(22.5)));

        return LayerDefinition.create(mesh, 32, 32);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int combinedLightIn, int combinedOverlayIn, boolean mirrored) {
        this.front_brace.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        this.back_brace.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        if (mirrored) {
            poseStack.pushPose();
            poseStack.rotateAround(new Quaternionf(new AxisAngle4d(Math.toRadians(180), 0, 0, 1)), (float) 8 /16, (float) 6 /16, 0);
        }
        this.connector.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        this.connector_top.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        this.connector_bottom.render(poseStack, vertexConsumer, combinedLightIn, combinedOverlayIn);
        if (mirrored) poseStack.popPose();
    }
}
