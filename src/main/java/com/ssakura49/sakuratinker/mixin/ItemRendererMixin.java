package com.ssakura49.sakuratinker.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ssakura49.sakuratinker.client.baked.model.PerspectiveModel;
import com.ssakura49.sakuratinker.client.render.STToolRenders;
import com.ssakura49.sakuratinker.client.render.TicToolRender;
import com.ssakura49.sakuratinker.client.render.provider.context.ItemRenderContext;
import com.ssakura49.sakuratinker.client.render.shader.ShaderProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Unique
    ItemStack sakurashader$stack;
    @Unique
    VertexConsumer sakurashader$vertexConsumer;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0)
    )
    public void onRenderItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack mStack, MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel modelIn, CallbackInfo ci) {
        if (modelIn instanceof PerspectiveModel iItemRenderer) {
            mStack.pushPose();
            final PerspectiveModel renderer = (PerspectiveModel) iItemRenderer.applyTransform(context, mStack, leftHand);
            mStack.translate(-0.5D, -0.5D, -0.5D);
            renderer.renderItem(stack, context, mStack, buffers, packedLight, packedOverlay);
            mStack.popPose();
        }
    }

//    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
//    public void render(
//            ItemStack pItemStack,
//            ItemDisplayContext pDisplayContext,
//            boolean pLeftHand,
//            PoseStack pPoseStack,
//            MultiBufferSource pBuffer,
//            int pCombinedLight,
//            int pCombinedOverlay,
//            BakedModel pModel,
//            CallbackInfo ci
//    ) {
//        if (!TicToolRender.shouldRenderWithShader(pItemStack)) {
//            return;
//        }
//
//
//        Map<MaterialVariantId, ShaderProvider.Tool> materialShaderProviderMap = TicToolRender.collectShadersForMaterials(pItemStack);
//        Map<ModifierId, ShaderProvider.Tool> modifierShaderProviderMap = TicToolRender.collectShadersForModifiers(pItemStack);
//
//        if (materialShaderProviderMap.isEmpty() && modifierShaderProviderMap.isEmpty()) {
//            return;
//        }
//
//        ItemRenderContext itemRenderContext = new ItemRenderContext(
//                pItemStack,
//                pDisplayContext,
//                pLeftHand,
//                pPoseStack,
//                pBuffer,
//                pCombinedLight,
//                pCombinedOverlay
//        );
//
//        if (!materialShaderProviderMap.isEmpty()) {
//            materialShaderProviderMap.forEach((materialVariantId, shaderProvider) -> {
//                shaderProvider.prepareRenderItem(itemRenderContext);
//                shaderProvider.prepareRenderMaterial(materialVariantId);
//            });
//        }
//
//        final ToolStack tool;
//        if (!modifierShaderProviderMap.isEmpty()) {
//            tool = ToolStack.from(pItemStack);
//            modifierShaderProviderMap.forEach((modifierId, shaderProvider) -> {
//                shaderProvider.prepareRenderItem(itemRenderContext);
//                shaderProvider.prepareRenderModifier(tool, modifierId);
//            });
//        } else {
//            tool = null;
//        }
//
//        List<ShaderProvider.Tool> seenList = new ArrayList<>();
//
//        TicToolRender.renderQuadsTasks(pItemStack, pPoseStack, pModel, pDisplayContext, pLeftHand, (renderType, quads) ->
//                STToolRenders.prepareRenderTasks(
//                        renderType,
//                        quads,
//                        itemRenderContext,
//                        tool,
//                        seenList
//                ));
//
//        ci.cancel();
//    }

}