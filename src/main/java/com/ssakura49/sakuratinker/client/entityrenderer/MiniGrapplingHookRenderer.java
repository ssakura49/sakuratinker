package com.ssakura49.sakuratinker.client.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.common.entity.MiniGrapplingHookEntity;
import com.ssakura49.sakuratinker.common.tools.item.GrapplingHookItem;
import com.ssakura49.sakuratinker.register.STItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class MiniGrapplingHookRenderer extends EntityRenderer<MiniGrapplingHookEntity> {
    private final ItemRenderer itemRenderer;

    public MiniGrapplingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MiniGrapplingHookEntity entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        float lerpYRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float lerpXRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(lerpYRot));

        poseStack.mulPose(Axis.XP.rotationDegrees(-lerpXRot));

        poseStack.mulPose(Axis.YP.rotationDegrees(-90F));

        poseStack.mulPose(Axis.ZP.rotationDegrees(-45F));

        poseStack.mulPose(Axis.YP.rotationDegrees(180F));

        ItemStack stack = new ItemStack(STItems.grappling_blade.get());
        BakedModel model = itemRenderer.getModel(stack, entity.level(), null, entity.getId());
        itemRenderer.render(
                stack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                buffer,
                light,
                OverlayTexture.NO_OVERLAY,
                model
        );

        poseStack.popPose();
        renderChord(entity,partialTicks,poseStack,buffer,light);
    }

    public void renderChord(MiniGrapplingHookEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int packedLight) {

        UUID uuid = MiniGrapplingHookEntity.CASTERS.keySet().stream().filter(k->MiniGrapplingHookEntity.CASTERS.get(k).equals(entity)).findFirst().orElse(null);
        if (uuid == null) return;
        Player player = null;
        if (Minecraft.getInstance().level != null) {
            player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        }
        if (player != null) {
            boolean isMainHand = player.getMainHandItem().is(STItems.grappling_hook.get());
            boolean isOffHand = player.getMainHandItem().is(STItems.grappling_hook.get());
            stack.pushPose();
            int armDirection = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            if (isOffHand) {
                armDirection = -armDirection;
            }
            float attackAnimation = player.getAttackAnim(partialTicks);
            float interpolatedAttackSwing = Mth.sin(Mth.sqrt(attackAnimation ) * (float)Math.PI);
            float bodyYawRadians = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
            double bodyYawSin = Mth.sin(bodyYawRadians );
            double bodyYawCos = Mth.cos(bodyYawRadians );
            double offsetX = (double)armDirection * 0.35D;
            double hookX,hookY,hookZ;
            float crouchOffset;
            if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
                double fovFactor = 960.0D / (double)this.entityRenderDispatcher.options.fov().get().intValue();
                Vec3 cameraOffset = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)armDirection * 0.525F, -0.1F);
                cameraOffset = cameraOffset.scale(fovFactor);
                cameraOffset = cameraOffset.yRot(interpolatedAttackSwing  * 0.5F);
                cameraOffset = cameraOffset.xRot(-interpolatedAttackSwing  * 0.7F);
                hookX = Mth.lerp(partialTicks, player.xo, player.getX()) + cameraOffset.x;
                hookY = Mth.lerp(partialTicks, player.yo, player.getY()) + cameraOffset.y;
                hookZ = Mth.lerp(partialTicks, player.zo, player.getZ()) + cameraOffset.z;
                crouchOffset = player.getEyeHeight();
            } else {
                hookX = Mth.lerp(partialTicks, player.xo, player.getX()) - bodyYawCos * offsetX - bodyYawSin  * 0.8D;
                hookY = player.yo + (double)player.getEyeHeight() + (player.getY() - player.yo) * (double)partialTicks - 0.45D;
                hookZ = Mth.lerp(partialTicks, player.zo, player.getZ()) - bodyYawSin  * offsetX + bodyYawCos * 0.8D;
                crouchOffset = player.isCrouching() ? -0.1875F : 0.0F;
            }

            double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
            double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float deltaX = (float)(hookX - entityX);
            float deltaY = (float)(hookY - entityY) + crouchOffset;
            float deltaZ = (float)(hookZ - entityZ);
            VertexConsumer lineBuffer = source.getBuffer(RenderType.lineStrip());
            PoseStack.Pose poseStack = stack.last();
            int segments = 24;

            int color = 0xDDDDDD;

            for(int segment = 0; segment <= segments; ++segment) {
                stringVertex(deltaX, deltaY, deltaZ, lineBuffer, poseStack, fraction(segment, 24), fraction(segment + 1, 24),segment,color);
            }

            stack.popPose();
        }

    }

    private static float fraction(int segment, int a) {
        return (float)segment / (float)a;
    }


    private static void stringVertex(
            float deltaX, float deltaY, float deltaZ,
            VertexConsumer vertexBuffer, PoseStack.Pose pose,
            float currentSegmentFraction, float nextSegmentFraction, int currentSegment, int color) {

        float currentX = deltaX * currentSegmentFraction;
        float currentY = deltaY * (currentSegmentFraction * currentSegmentFraction + currentSegmentFraction) * 0.5F + 0.25F;
        float currentZ = deltaZ * currentSegmentFraction;

        float nextX = deltaX * nextSegmentFraction - currentX;
        float nextY = deltaY * (nextSegmentFraction * nextSegmentFraction + nextSegmentFraction) * 0.5F + 0.25F - currentY;
        float nextZ = deltaZ * nextSegmentFraction - currentZ;

        float segmentLength = Mth.sqrt(nextX * nextX + nextY * nextY + nextZ * nextZ);
        nextX /= segmentLength;
        nextY /= segmentLength;
        nextZ /= segmentLength;

        float stringR = (color >> 16& 255) / 255f;
        float stringG = (color >> 8 & 255) / 255f;
        float stringB = (color & 255) / 255f;

        float r = stringR;
        float g = stringG;
        float b = stringB;

        if (currentSegment % 2 == 0) {
            r *= 0.7f;
            g *= 0.7f;
            b *= 0.7f;
        }

        vertexBuffer.vertex(pose.pose(), currentX, currentY, currentZ)
                .color(r, g, b,1)
                .normal(pose.normal(), nextX, nextY, nextZ)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MiniGrapplingHookEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
