/**package com.ssakura49.sakuratinker.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.client.model.TerraPrismaModel;
import com.ssakura49.sakuratinker.common.entity.terraprisma.TerraPrismEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class TerraPrismaRenderer extends EntityRenderer<TerraPrismEntity> {
    private final TerraPrismaModel<TerraPrismEntity> model;
    private final ResourceLocation TEXTURE = new ResourceLocation(SakuraTinker.MODID, "textures/model/terra_prisma.png");

    public TerraPrismaRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TerraPrismaModel<>(context.bakeLayer(TerraPrismaModel.LAYER_LOCATION));
    }

    @Override
    public void render(TerraPrismEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        Vec3 smoothedPos = entity.getSmoothedPosition(partialTicks);
        poseStack.translate(
                smoothedPos.x - entity.getX(),
                smoothedPos.y - entity.getY() + 0.4,
                smoothedPos.z - entity.getZ()
        );
        applyMovementRotation(entity, poseStack, partialTicks);
        applyAnimationEffects(entity, poseStack, partialTicks);
        float hue = ((entity.tickCount + partialTicks) * 3 % 360) / 360f;
        int rgbColor = Mth.hsvToRgb(hue, 0.8f, 1.0f);
        float r = ((rgbColor >> 16) & 0xFF) / 255f;
        float g = ((rgbColor >> 8) & 0xFF) / 255f;
        float b = (rgbColor & 0xFF) / 255f;

        VertexConsumer solidConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(
                poseStack, solidConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                r, g, b, 1.0f
        );

        poseStack.pushPose();
        float pulseSpeed = 0.5f;
        float pulseScale = 1.0f + Mth.sin((entity.tickCount + partialTicks) * pulseSpeed) * 0.2f;
        poseStack.scale(pulseScale, pulseScale, pulseScale);

        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        model.renderToBuffer(
                poseStack, glowConsumer, 15728880,
                OverlayTexture.NO_OVERLAY,
                r, g, b, 0.3f
        );
        poseStack.popPose();

        poseStack.popPose();
    }

    private void applyMovementRotation(TerraPrismEntity entity, PoseStack poseStack, float partialTicks) {
        LivingEntity target = entity.getTarget();
        if (target != null && !target.isDeadOrDying()) {
            //面向目标
            Vec3 targetPos = target.getEyePosition(partialTicks);
            Vec3 entityPos = entity.getEyePosition(partialTicks);
            Vec3 dir = targetPos.subtract(entityPos).normalize();
            float yaw = (float) Math.atan2(dir.x, dir.z);
            float pitch = (float) Math.asin(dir.y);
            poseStack.mulPose(Axis.YP.rotation(yaw));
            poseStack.mulPose(Axis.XP.rotation(-pitch));
        } else {
            //面向移动方向
            Vec3 motion = entity.getDeltaMovement();
            if (motion.lengthSqr() > 0.001) {
                float yaw = (float) Math.atan2(motion.x, motion.z);
                poseStack.mulPose(Axis.YP.rotation(yaw));
            }
        }
    }

    private void applyAnimationEffects(TerraPrismEntity entity, PoseStack poseStack, float partialTicks) {
        String state = entity.getAnimationState();
        float progress = entity.getAnimationProgress(partialTicks);
        switch (state) {
            case "charge" -> {
                //冲刺
                poseStack.scale(1.4f, 0.7f, 1.4f);
                poseStack.mulPose(Axis.YP.rotation(progress * Mth.TWO_PI * 2));
            }
            case "slash" -> {
                //挥砍
                poseStack.mulPose(Axis.ZP.rotation(Mth.sin(progress * Mth.PI) * 0.8f));
                poseStack.scale(1.2f, 1.2f, 1.2f);
            }
            default -> {
                //空闲状态：缓慢浮动和旋转
                float floatOffset = Mth.sin((entity.tickCount + partialTicks) * 0.15f) * 0.2f;
                float rotateSpeed = entity.getDeltaMovement().lengthSqr() > 0.1 ? 10f : 4f;
                poseStack.translate(0, floatOffset, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 0.4f));
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TerraPrismEntity entity) {
        return TEXTURE;
    }
}

 */
