package com.ssakura49.sakuratinker.client.baked.loader;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ssakura49.sakuratinker.client.render.STToolRenders;
import com.ssakura49.sakuratinker.client.render.provider.context.ItemRenderContext;
import com.ssakura49.sakuratinker.client.render.provider.context.armor.RenderArmorPartContext;
import com.ssakura49.sakuratinker.client.render.provider.context.tool.RenderGenericContext;
import com.ssakura49.sakuratinker.client.render.provider.context.tool.RenderQuadContext;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IArmorPartContextRenderer;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IGenericRenderer;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IQuadContextRenderer;
import com.ssakura49.sakuratinker.client.render.shader.ShaderProvider;
import com.ssakura49.sakuratinker.render.shader.cosmic.TicCosmicShader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Objects;

public class TicCosmicModelShaderProvider {
    private static TicCosmicShader shader;

    public static void init(IEventBus eventBus) {
        shader = new TicCosmicShader();
    }

    public static class Material extends ShaderProvider.Tool {
        private VertexConsumer buffer;

        @Override
        public void renderOverlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {
            VertexConsumer consumer = Objects.requireNonNullElseGet(buffer, quadContext::getConsumer);

            renderer.render(quadContext.renderContext(), quadContext.quad(), consumer);
        }

        @Override
        public void renderUnderlay(RenderQuadContext quadContext, IQuadContextRenderer consumer) {
        }

        @Override
        public void prepareRenderItem(ItemRenderContext context) {
            buffer = null;
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, STToolRenders.RenderPhase phase) {
            RenderType renderType = shader.getCosmicRenderType();
            buffer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniform(InventoryMenu.BLOCK_ATLAS,
                    context.displayContext() == ItemDisplayContext.GUI);
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, STToolRenders.RenderPhase phase) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }

    public static class Armor extends ShaderProvider.Armor {
        @Override
        public void renderOverlay(RenderArmorPartContext quadContext, IArmorPartContextRenderer renderer) {
            VertexConsumer buffer = quadContext.material().buffer(
                    quadContext.renderContext().bufferSource(),
                    shader::getCosmicRenderTypeArmor
            );



            shader.setupUniform(quadContext.material().atlasLocation(), false);

            renderer.render(
                    quadContext.renderContext(),
                    quadContext.model(),
                    buffer
            );
        }

        @Override
        public void renderUnderlay(RenderArmorPartContext quadContext, IArmorPartContextRenderer bakedConsumer) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }

    public static TicCosmicShader getShader() {
        return shader;
    }

    public static class Generic extends ShaderProvider.Generic {

        @Override
        public void renderOverlay(RenderGenericContext context, IGenericRenderer renderer) {
            VertexConsumer vertexConsumer = context.bufferGetter().get(shader.getCosmicRenderType());

            shader.setupUniform(context.atlasLocation(), context.onGui());

            renderer.render(
                    vertexConsumer, context.renderContext(),
                    1.0f, 1.0f, 1.0f, 1.0f
            );
        }

        @Override
        public void renderUnderlay(RenderGenericContext quadContext, IGenericRenderer renderer) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }
}
