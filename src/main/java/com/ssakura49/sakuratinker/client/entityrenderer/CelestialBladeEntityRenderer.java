package com.ssakura49.sakuratinker.client.entityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.common.entity.CelestialBladeEntity;
import com.ssakura49.sakuratinker.common.entity.item.CelestialBladePart;
import com.ssakura49.sakuratinker.utils.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@OnlyIn(Dist.CLIENT)
public class CelestialBladeEntityRenderer extends EntityRenderer<CelestialBladeEntity> {
    private static final ResourceLocation TRAIL_TEXTURE = SakuraTinker.getResource("textures/entity/trail.png");
    private static final int DIVISION = 128;
    private static final float CELL = (float) 360 / DIVISION;
    private static final int CELL_AMOUNT = Math.round(120 / CELL);
    private final ItemRenderer itemRenderer;

    public CelestialBladeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public boolean shouldRender(@NotNull CelestialBladeEntity livingEntity, @NotNull Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(CelestialBladeEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        CelestialBladePart part = entity.getZenithPart();
        poseStack.pushPose();
        Entity owner = entity.level().getEntity(entity.getOwnerId());
        if (owner != null) {
            Vec3 pos = new Vec3(
                    Mth.lerp(partialTicks, entity.xo, entity.getX()),
                    Mth.lerp(partialTicks, entity.yo, entity.getY()),
                    Mth.lerp(partialTicks, entity.zo, entity.getZ())
            );
            Vec3 ownerPos = new Vec3(
                    Mth.lerp(partialTicks, owner.xo, owner.getX()),
                    Mth.lerp(partialTicks, owner.yo, owner.getY()),
                    Mth.lerp(partialTicks, owner.zo, owner.getZ())
            );
            Vec3 difference = entity.getIdealPos(owner, ownerPos).subtract(pos);
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && owner == Minecraft.getInstance().cameraEntity) {
                Vec3 offset = MathUtils.rotationToPosition(owner.getBbHeight() * 0.1f, -Minecraft.getInstance().cameraEntity.getXRot() - 90, Minecraft.getInstance().cameraEntity.getYHeadRot() + 90);
                difference = difference.add(offset);
            }
            poseStack.translate(difference.x, difference.y, difference.z);

        }
        float attackYaw = Mth.lerp(partialTicks, entity.prevYaw, entity.renderYaw) * Mth.DEG_TO_RAD;
        float attackPitch = Mth.lerp(partialTicks, entity.prevPitch, entity.renderPitch) * Mth.DEG_TO_RAD;
        poseStack.mulPose(Axis.XP.rotation(Mth.HALF_PI));
        poseStack.mulPose(Axis.ZP.rotation(attackYaw - Mth.HALF_PI));
        poseStack.mulPose(Axis.XN.rotation(attackPitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.prevRoll, entity.renderRoll)));
        float age = entity.getClientAge() + partialTicks;
        float progress = Mth.clamp(age / CelestialBladeEntity.LIFESPAN, 0, 1);
//        float innerRadius = ZenithSlashEntity.LENGTH / 2f;
        float innerRadius = entity.getRange() / 2f;
        float outerRadiusOffset = (float) part.trailWidth();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        ItemStack renderStack = entity.getItemStack();
        int trailColor = part.color();
        if (!renderStack.isEmpty() && renderStack.getItem() instanceof ModifiableItem) {
            ToolStack tool = ToolStack.from(renderStack);
            if (!tool.getMaterials().isEmpty()) {
                MaterialVariant firstMat = tool.getMaterial(0);
                trailColor = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(firstMat.getVariant())
                        .map(MaterialRenderInfo::vertexColor)
                        .orElse(part.color());
            }
        }

        for (int i = 0; i < CELL_AMOUNT; i++) {
            float rotation = progress * Mth.TWO_PI - i * CELL * Mth.DEG_TO_RAD;
            float nextRotation = progress * Mth.TWO_PI - (i + 1) * CELL * Mth.DEG_TO_RAD;
            if (rotation < 0) {
                continue;
            }
            if (nextRotation < 0) {
                nextRotation = 0;
            }
            Vec3 dir = new Vec3(Mth.sin(rotation) * CelestialBladeEntity.RADIUS_RATIO, -Mth.cos(rotation), 0);
            Vec3 nextDir = new Vec3(Mth.sin(nextRotation) * CelestialBladeEntity.RADIUS_RATIO, -Mth.cos(nextRotation), 0);
            float centerProgress = Mth.sin((1 - 2 * Math.abs(rotation / Mth.TWO_PI - 0.5f)) * Mth.HALF_PI);
            float nextCenterProgress = Mth.sin((1 - 2 * Math.abs(nextRotation / Mth.TWO_PI - 0.5f)) * Mth.HALF_PI);
            Vec3 innerPos = new Vec3(0, innerRadius, 0).add(dir.scale(innerRadius));
            Vec3 outerPos = new Vec3(0, innerRadius, 0).add(dir.normalize().scale(dir.length() * innerRadius + outerRadiusOffset * centerProgress));
            Vec3 nextInnerPos = new Vec3(0, innerRadius, 0).add(nextDir.scale(innerRadius));
            Vec3 nextOuterPos = new Vec3(0, innerRadius, 0).add(nextDir.normalize().scale(nextDir.length() * innerRadius + outerRadiusOffset * nextCenterProgress));
            float alpha = 1 - (float) i / CELL_AMOUNT;
            float nextAlpha = 1 - (float) (i + 1) / CELL_AMOUNT;
            float startProgress = 1 - 2 * Math.abs(progress - 0.5f);
            if (startProgress < 0.25) {
                alpha *= 1 - (0.25f - startProgress) * 4;
                nextAlpha *= 1 - (0.25f - startProgress) * 4;
            }
            consumer.vertex(pose.pose(), innerPos.toVector3f().x(), innerPos.toVector3f().y(), innerPos.toVector3f().z()).color(withAlpha(Math.round(alpha * 255), trailColor)).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
            consumer.vertex(pose.pose(), outerPos.toVector3f().x(), outerPos.toVector3f().y(), outerPos.toVector3f().z()).color(withAlpha(Math.round(alpha * 255), trailColor)).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
            consumer.vertex(pose.pose(), nextOuterPos.toVector3f().x(), nextOuterPos.toVector3f().y(), nextOuterPos.toVector3f().z()).color(withAlpha(Math.round(nextAlpha * 255), trailColor)).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
            consumer.vertex(pose.pose(), nextInnerPos.toVector3f().x(), nextInnerPos.toVector3f().y(), nextInnerPos.toVector3f().z()).color(withAlpha(Math.round(nextAlpha * 255), trailColor)).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        }
        float rotation = progress * Mth.TWO_PI;
        Vec3 dir = new Vec3(Mth.sin(rotation) * CelestialBladeEntity.RADIUS_RATIO, -Mth.cos(rotation), 0);
        float centerProgress = Mth.sin((1 - 2 * Math.abs(rotation / Mth.TWO_PI - 0.5f)) * Mth.HALF_PI);
        Vec3 itemPos = new Vec3(0, innerRadius, 0).add(dir.normalize().scale(dir.length() * innerRadius + outerRadiusOffset * centerProgress / 2));
        //ItemStack itemStack = part.item().value().getDefaultInstance();

        BakedModel bakedModel = this.itemRenderer.getModel(renderStack, entity.level(), null, entity.getId());
        poseStack.translate(itemPos.x, itemPos.y, itemPos.z);
        poseStack.translate(0, -part.rotationCenterHeight(), 0);
        poseStack.mulPoseMatrix(new Matrix4f().translate(0, (float) part.rotationCenterHeight(), 0)
                .scale((float) (part.scale() * centerProgress))
                .rotate((float) Mth.atan2(dir.y, dir.x) - (float) part.rotation(), 0.0f, 0.0f, 1.0f)
                .translate(0, (float) -part.rotationCenterHeight(), 0));
        itemRenderer.render(renderStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, bakedModel);
        poseStack.popPose();
    }

    private static int withAlpha(int alpha, int color) {
        return alpha << 24 | color & 16777215;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CelestialBladeEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
