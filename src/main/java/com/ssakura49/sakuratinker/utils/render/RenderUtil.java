package com.ssakura49.sakuratinker.utils.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class RenderUtil {
    public static RenderType brightProjectileRenderType(ResourceLocation texture){
        return RenderType.create("bright_projectile",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
                RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .createCompositeState(true));
    }

    public static RenderType debugSolid() {
        return RenderType.create("debug_solid",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setTextureState(NO_TEXTURE)
                        .setCullState(NO_CULL)
                        .createCompositeState(true));
    }

    public static void drawPipe(PoseStack poseStack, VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                float radius, float distance, int r, int g, int b, int a) {
        for (int i = 0; i < 4; i++) {
            drawQuad(consumer, poseMatrix, normalMatrix, radius, distance, r, g, b, a);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        }
    }

    private static void drawQuad(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                 float radius, float distance, int r, int g, int b, int a) {
        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0, 1, 0).endVertex();

        consumer.vertex(poseMatrix, -radius,  radius, 0).color(r,g,b,a).uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0, 1, 0).endVertex();

        consumer.vertex(poseMatrix, -radius,  radius, distance).color(r,g,b,a).uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0, 1, 0).endVertex();

        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0, 1, 0).endVertex();
    }
//    public static void drawPipe(PoseStack pPoseStack, VertexConsumer consumer, Matrix4f poseMatrix, float radius, float distance, int r, int g, int b, int a, Matrix3f normalMatrix){
//        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, 0).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, radius, radius, 0).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, radius, -radius, 0).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//
//        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, distance).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, radius, radius, distance).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, radius, -radius, distance).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//
//        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, 0).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, distance).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
//        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, 0).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, distance).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
//        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, 0).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, distance).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
//        consumer.vertex(poseMatrix, -radius, -radius, 0).color(r,g,b,a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, 0).color(r,g,b,a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, radius, distance).color(r,g,b,a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        consumer.vertex(poseMatrix, -radius, -radius, distance).color(r,g,b,a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0, 1, 0).endVertex();
//        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
//    }
}
