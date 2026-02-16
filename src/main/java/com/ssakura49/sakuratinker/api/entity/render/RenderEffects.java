package com.ssakura49.sakuratinker.api.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.render.RendererUtils;
import com.ssakura49.sakuratinker.render.shader.GlowRenderLayer;
import com.ssakura49.sakuratinker.render.shader.STRenderType;
import com.ssakura49.sakuratinker.utils.time.TimeContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RenderEffects {
    public static class RegularPolygon {
        private static final List<FloatingSquare> FLOATING_SQUARES = new ArrayList<>();
        private static final Random RANDOM = new Random();
        private static final RenderType ring = new GlowRenderLayer(STRenderType.createSphereRenderType2(RendererUtils.runes), null, 1, false);

        /** 漂浮小方块 */
        public static class FloatingSquare {
            float x, z;
            float y;
            float size;
            float age;
            float lifetime;

            FloatingSquare(float radius, float maxSize) {
                float angle = RANDOM.nextFloat() * 360f;
                this.x = (float)(radius * Math.cos(Math.toRadians(angle)));
                this.z = (float)(radius * Math.sin(Math.toRadians(angle)));
                this.y = 0f;
                this.size = 0.3f + RANDOM.nextFloat() * (maxSize - 0.3f);
                this.age = 0f;
                this.lifetime = 40f + RANDOM.nextFloat() * 40f; // 存活 40~80 tick
            }

            float progress() {
                return age / lifetime;
            }
        }

        /**
         * 公共渲染方法：三角/四边/五边/圆环 + 漂浮方块
         */
        public static void render(PoseStack poseStack, MultiBufferSource.BufferSource source) {
            poseStack.pushPose();
            float degrees = TimeContext.Client.getCommonDegrees();
            float d1 = degrees * 2.5F;
            float d2 = degrees / 20;
            //MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers.bufferSource();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.8f);
            //poseStack.mulPose(Axis.YP.rotationDegrees(30));
            poseStack.mulPose(Axis.ZP.rotationDegrees(d1));
            poseStack.translate(0, 0, 0.01F);
            RendererUtils.renderRegularPolygon(poseStack, source, 1f, 3, 0.3f, 1024, 1, 1, 1, 1, STRenderType.END_PORTAL(RendererUtils.cosmic), 1f, true);
            poseStack.translate(0, 0, -0.01F);
            RendererUtils.renderRegularPolygon(poseStack, source, 0.8f, 3, 0.22f, 1024, 1, 1, 1, 1, true);
            RendererUtils.renderRegularPolygon(poseStack, source, 1.2f, 3, 0.22f, 1024, 0.6f, 0.8f, 0.7f, 1, true);
            poseStack.popPose();

            //四边
            poseStack.pushPose();
            poseStack.translate(0f, 0f, 1.1f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-degrees * 1.2F));
            RendererUtils.renderRegularPolygon(poseStack, source, 2f, 4, 0.3f, 1024, .6f, .6f, 1, 0.8F, STRenderType.END_PORTAL(RendererUtils.cosmic), 1, true);
            poseStack.translate(0, 0, -0.01F);
            RendererUtils.renderRegularPolygon(poseStack, source, 1.8f, 4, 0.22f, 1024, 1, 1, 1, 1, true);
            //RendererUtils.renderRegularPolygon(poseStack, source, 2.2f, 4, 0.22f, 1024, 0.6f, 0.8f, 0.7f, 1, true);
            poseStack.popPose();

            //五边
            poseStack.pushPose();
            poseStack.translate(0f, 0f, 1.4f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(degrees));
            RendererUtils.renderRegularPolygon(poseStack, source, 3f, 5, 0.4f, 1024, .4f, .4f, .4f, .4f, STRenderType.END_PORTAL(RendererUtils.cosmic), 1, true);
            poseStack.translate(0, 0, -0.01F);
            //RendererUtils.renderRegularPolygon(poseStack, source, 2.8f, 5, 0.16f, 1024, 1, 1, 1, 1, true);
            RendererUtils.renderRegularPolygon(poseStack, source, 3.2f, 5, 0.16f, 1024, 0.6f, 0.8f, 0.7f, 1, true);
            poseStack.popPose();

            //圆环
            poseStack.pushPose();
            poseStack.translate(0f, 0f, 1.7f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90F));
            poseStack.mulPose(Axis.YP.rotationDegrees(degrees * 2.4936F));
            RendererUtils.renderRing(poseStack, source, 0.0D, 90.0F, 2.6f, 2.4f / (199F / 16F), 64, 128, 128, .6f, .6f, .6f, .7F, ring);
            poseStack.popPose();

            renderFloatingSquares(poseStack, source);

            poseStack.popPose();
            RenderSystem.disableDepthTest();
            source.endBatch();
        }

        /** 漂浮小方块渲染 */
        public static void renderFloatingSquares(PoseStack poseStack, MultiBufferSource.BufferSource source) {
            float radius = 2f;
            float maxSize = 0.4f;

            if (RANDOM.nextFloat() < 0.2f) {
                FLOATING_SQUARES.add(new FloatingSquare(radius, maxSize));
            }

            Iterator<FloatingSquare> iter = FLOATING_SQUARES.iterator();
            while (iter.hasNext()) {
                FloatingSquare sq = iter.next();
                sq.age++;
                if (sq.age > sq.lifetime) {
                    iter.remove();
                    continue;
                }

                float t = sq.progress();
                float y = sq.y + t * 1.5f;
                float scale = sq.size * (1.0f - t);
                float alpha = 1.0f - t;

                poseStack.pushPose();
                poseStack.translate(sq.x, y, sq.z);

                poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.scale(scale, scale, scale);

                RendererUtils.renderRegularPolygon(
                        poseStack,
                        source,
                        scale, 4, scale, 1024,
                        1f, 1f, 1f, alpha,
                        STRenderType.END_PORTAL(RendererUtils.cosmic),
                        1f, true
                );
                poseStack.popPose();
            }
        }
    }
}
