package com.ssakura49.sakuratinker.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.client.render.TicToolRender;
import com.ssakura49.sakuratinker.client.render.provider.ArmorContextRenderer;
import com.ssakura49.sakuratinker.client.render.provider.context.RenderContext;
import com.ssakura49.sakuratinker.client.render.provider.context.armor.RenderArmorPartContext;
import com.ssakura49.sakuratinker.client.render.shader.ShaderProvider;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Optional;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    public void renderTrim(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, ArmorTrim armorTrim, Model model, boolean pInnerTexture, CallbackInfo ci) {
        Holder<TrimMaterial> material = armorTrim.material();
        Optional<ResourceKey<TrimMaterial>> trimMaterialKeyOpt = material.unwrapKey();
        if (trimMaterialKeyOpt.isEmpty()) {
            return;
        }
        ResourceKey<TrimMaterial> trimMaterialKey = trimMaterialKeyOpt.get();
        ResourceLocation trimMaterialId = trimMaterialKey.location();

        MaterialVariantId materialVariantId = MaterialVariantId.tryParse(trimMaterialId.toString());
        if(materialVariantId != null && STConfig.Client.ENABLE_COSMIC_RENDERER.get()) {
            MaterialId id = materialVariantId.getId();

            ShaderProvider.Armor shaderProvider = TicToolRender.ARMOR_SHADERS.getShaderProvider(id);

            if (shaderProvider != null) {
                Material textureMaterial = new Material(
                        Sheets.ARMOR_TRIMS_SHEET,
                        pInnerTexture ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial)
                );

                RenderContext renderContext = new RenderContext(
                        bufferSource,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        poseStack, pPackedLight, OverlayTexture.NO_OVERLAY
                );
                shaderProvider.renderOverlay(new RenderArmorPartContext(
                        renderContext,
                        model,
                        textureMaterial,
                        false
                ), ArmorContextRenderer.RENDERER);
            }

            ci.cancel();
        }
    }
}
