package com.ssakura49.sakuratinker.client.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.common.entity.CelestialBladeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CelestialBladeRenderer extends EntityRenderer<CelestialBladeProjectile> {
    private final ItemRenderer itemRenderer;

    public static final Quaternionf AXIS;
    public static final double SIN45 = Math.sin(Math.toRadians(45.0));
    static {
        double a = Math.toRadians(90.0) * 0.5;
        double sin = Math.sin(a);
        double cos = Math.cos(a);
        AXIS = new Quaternionf(-sin * SIN45, sin * SIN45, 0.0, cos);
    }

    public CelestialBladeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CelestialBladeProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Vec3 center = entity.ellipseCenter;
        Vec3 pos = entity.position();
        Vec3 dir;
        if (center != null) {
            dir = pos.subtract(center).normalize();
        } else {
            // fallback：用运动方向
            dir = entity.getDeltaMovement().normalize();
            if (dir.lengthSqr() < 1e-6) dir = new Vec3(0, 0, 1);
        }

        // -------- 2. 转换成 quaternion --------
        Vector3f modelForward = new Vector3f(0, 0, 1); // 模型默认Z+为剑刃
        Vector3f targetDir = new Vector3f((float) dir.x, (float) dir.y, (float) dir.z);
        Quaternionf rotation = new Quaternionf().rotationTo(modelForward, targetDir);
        poseStack.mulPose(rotation);

        poseStack.scale(2.0F, 2.0F, 2.0F);
        //poseStack.translate(0.0D, 0.569D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-135.0F));
        //poseStack.translate(0.203D, 0.078D, 0.0D);

        // -------- 4. 渲染物品模型 --------
        ItemStack stack = entity.getItem();
        this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();

        // -------- 5. 渲染拖尾 --------
        PoseStack trailStack = new PoseStack();
        trailStack.pushPose();
        trailStack.translate(entity.getX(), entity.getY(), entity.getZ());
        trailStack.mulPose(rotation); // 拖尾也保持同方向
        entity.trail.renderTrail(entity, entity.position(), trailStack, buffer, packedLight, null, Vec3.ZERO);
        trailStack.popPose();
    }


    protected void renderTrail(CelestialBladeProjectile entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, PoseStack.Pose pose){
        entity.trail.renderTrail(entity, entity.position(), poseStack, bufferSource, packedLight, pose, Vec3.ZERO);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CelestialBladeProjectile entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
