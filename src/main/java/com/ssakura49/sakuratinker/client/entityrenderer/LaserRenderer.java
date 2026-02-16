package com.ssakura49.sakuratinker.client.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.entity.LaserProjectileEntity;
import com.ssakura49.sakuratinker.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LaserRenderer extends EntityRenderer<LaserProjectileEntity> {

    private static final ResourceLocation LASER_TEXTURE = ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "textures/entity/laser.png");

//    public static final RenderType LASER_RENDER_TYPE = RenderType.create(
//            "laser",
//            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
//            VertexFormat.Mode.QUADS,
//            256,
//            false,
//            true,
//            RenderType.CompositeState.builder()
//                    .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
//                    .setTextureState(new RenderStateShard.TextureStateShard(LASER_TEXTURE, false, false))
//                    .setCullState(RenderStateShard.NO_CULL)
//                    .setLightmapState(RenderStateShard.LIGHTMAP)
//                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                    .createCompositeState(false)
//    );

    public LaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


//    @Override
//    public void render(LaserProjectileEntity entity, float entityYaw, float partialTicks,
//                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//        poseStack.pushPose();
//
//        // 起点就在实体位置
//        VertexConsumer consumer = buffer.getBuffer(RenderUtil.debugSolid());
//        RenderUtil.drawPipe(poseStack, consumer, poseStack.last().pose(),poseStack.last().normal(), 0.2f, 5f, 255, 0, 0, 255);
//
//        poseStack.popPose();
//    }


    @Override
    public void render(LaserProjectileEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Vec3 start = entity.getStartPos();
        Vec3 end   = entity.getEndPos();

        Player shooter = entity.getOwner() instanceof Player p ? p : null;
        if (shooter != null) {
            Vec3 eyeOffset = shooter.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(0.3f);
            if (eyeOffset.length() == 0) {
                eyeOffset = new Vec3(0.3, 0, 0);
            }

            InteractionHand hand = shooter.getUsedItemHand();
            if (hand == InteractionHand.OFF_HAND) {
                eyeOffset = eyeOffset.reverse();
            }
            start = start.add(eyeOffset);
        }

        Vec3 vec = end.subtract(start);
        float distance = (float) vec.length();

        // 平移到光束起点（注意要转到相机视角坐标系）
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(start.x - camPos.x, start.y - camPos.y, start.z - camPos.z);

        // 旋转对准目标方向
        double d0 = vec.horizontalDistance();
        float yRot = (float)(Mth.atan2(vec.x, vec.z) * (180F/Math.PI));
        float xRot = (float)(Mth.atan2(-vec.y, d0) * (180F/Math.PI));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        // 内核光束
        VertexConsumer consumer = buffer.getBuffer(RenderType.beaconBeam(getTextureLocation(entity), false));
        PoseStack.Pose pose = poseStack.last();
        RenderUtil.drawPipe(poseStack, consumer, pose.pose(), pose.normal(), 0.02f, distance, 255, 0, 0, 200);

        // 外层光晕
        consumer = buffer.getBuffer(RenderType.beaconBeam(getTextureLocation(entity), true));
        RenderUtil.drawPipe(poseStack, consumer, pose.pose(), pose.normal(), 0.05f, distance, 255, 100, 80, 128);

        poseStack.popPose();
    }
//    @Override
//    public void render(LaserProjectileEntity entity, float entityYaw, float partialTicks,
//                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//        Vec3 start = entity.getStartPos();
//        Vec3 end   = entity.getEndPos();
//
//        Vec3 vec = end.subtract(start);
//        float distance = (float) vec.length();
//
//        // 平移到光束起点（注意要转到相机视角坐标系）
//        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        poseStack.pushPose();
//        poseStack.translate(start.x - camPos.x, start.y - camPos.y, start.z - camPos.z);
//
//        // 旋转对准目标方向
//        double d0 = vec.horizontalDistance();
//        float yRot = (float)(Mth.atan2(vec.x, vec.z) * (180F/Math.PI));
//        float xRot = (float)(Mth.atan2(-vec.y, d0) * (180F/Math.PI));
//        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
//        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
//
//        // 内核光束
//        VertexConsumer consumer = buffer.getBuffer(RenderType.beaconBeam(getTextureLocation(entity), false));
//        PoseStack.Pose pose = poseStack.last();
//        RenderUtil.drawPipe(poseStack, consumer, pose.pose(), pose.normal(), 0.02f, distance, 255, 0, 0, 200);
//
//        // 外层光晕
//        consumer = buffer.getBuffer(RenderType.beaconBeam(getTextureLocation(entity), true));
//        RenderUtil.drawPipe(poseStack, consumer, pose.pose(), pose.normal(), 0.05f, distance, 255, 100, 80, 128);
//
//        poseStack.popPose();
//    }
    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull LaserProjectileEntity entity) {
        return LASER_TEXTURE;
    }

    @Override
    public int getBlockLightLevel(LaserProjectileEntity pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    public int getSkyLightLevel(LaserProjectileEntity pEntity, BlockPos pPos) {
        return 15;
    }
}
