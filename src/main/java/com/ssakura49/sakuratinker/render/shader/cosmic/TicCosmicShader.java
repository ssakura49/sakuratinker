package com.ssakura49.sakuratinker.render.shader.cosmic;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TicCosmicShader {
    private final RenderStateShard.ShaderStateShard stateShard;
    private final RenderType cosmicRenderType;
    private final Map<ResourceLocation, RenderType> cosmicArmorRenderTypeCache = new HashMap<>();

    public int internalRenderTime;
    public float internalRenderFrame;

    public ShaderInstance shaderInstance;
    public Uniform cosmicTime;
    public Uniform cosmicYaw;
    public Uniform cosmicPitch;
    public Uniform cosmicExternalScale;
    public Uniform cosmicOpacity;
    public Uniform cosmicUVs;

    public final Function<ResourceLocation, float[]> cosmicUVGetter = Util.memoize(resourceLocation -> {
        float[] cosmicUV = new float[VanillaCosmicShaders.COSMIC_UVS.length];
        for (int i = 0; i < VanillaCosmicShaders.COSMIC_SPRITES.length; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(resourceLocation)
                    .apply(SakuraTinker.getResource("item/misc/cosmic_" + i));
            cosmicUV[i * 4] = sprite.getU0();
            cosmicUV[i * 4 + 1] = sprite.getV0();
            cosmicUV[i * 4 + 2] = sprite.getU1();
            cosmicUV[i * 4 + 3] = sprite.getV1();
        }
        return cosmicUV;
    });

    public TicCosmicShader() {
        this.stateShard = new RenderStateShard.ShaderStateShard(() -> VanillaCosmicShaders.cosmicShader);
        this.cosmicRenderType = RenderType.create(
                "sakuratinker:tic_cosmic",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(stateShard)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .createCompositeState(true)
        );
    }

    public void initialize() {

    }

    public ShaderInstance getShaderInstance() {
        return shaderInstance;
    }

    public RenderType getCosmicRenderType() {
        return cosmicRenderType;
    }

    public RenderType getCosmicRenderTypeArmor(ResourceLocation texture) {
        return cosmicArmorRenderTypeCache.computeIfAbsent(texture, tex ->
                RenderType.create(
                        "sakuratinker:cosmic_armor",
                        DefaultVertexFormat.NEW_ENTITY, // 盔甲使用 NEW_ENTITY 格式
                        VertexFormat.Mode.QUADS,
                        256,
                        true,
                        false,
                        RenderType.CompositeState.builder()
                                .setShaderState(stateShard)
                                .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                                .setLightmapState(RenderStateShard.LIGHTMAP)
                                .setCullState(RenderStateShard.NO_CULL)
                                .setOverlayState(RenderStateShard.OVERLAY)
                                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                                .createCompositeState(true)
                )
        );
    }

    /**
     * @param atlas 当前渲染使用的图集位置
     * @param onGui 是否在 GUI 中渲染（决定了缩放倍率）
     */
    public void setupUniform(ResourceLocation atlas, boolean onGui) {
        ItemDisplayContext context = onGui ? ItemDisplayContext.GUI : ItemDisplayContext.NONE;
        VanillaCosmicShaders.updateShaderData(context);
        if (VanillaCosmicShaders.cosmicUVs != null) {
            float[] uvs = new float[40];
            for (int i = 0; i < 10; i++) {
                TextureAtlasSprite sprite = Minecraft.getInstance()
                        .getTextureAtlas(atlas)
                        .apply(CosmicTextures.rl(i));
                uvs[i * 4] = sprite.getU0();
                uvs[i * 4 + 1] = sprite.getV0();
                uvs[i * 4 + 2] = sprite.getU1();
                uvs[i * 4 + 3] = sprite.getV1();
            }
            VanillaCosmicShaders.cosmicUVs.glUniformF(false, uvs);
        }
    }
}
