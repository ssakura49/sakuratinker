package com.ssakura49.sakuratinker.client.render.shader;

import com.ssakura49.sakuratinker.client.render.STToolRenders;
import com.ssakura49.sakuratinker.client.render.provider.context.ItemRenderContext;
import com.ssakura49.sakuratinker.client.render.provider.context.armor.RenderArmorPartContext;
import com.ssakura49.sakuratinker.client.render.provider.context.tool.RenderGenericContext;
import com.ssakura49.sakuratinker.client.render.provider.context.tool.RenderQuadContext;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IArmorPartContextRenderer;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IGenericRenderer;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IQuadContextRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public abstract class ShaderProvider<RENDER_CONTEXT, RENDERER> {
    public abstract void renderOverlay(RENDER_CONTEXT context, RENDERER renderer);

    public abstract void renderUnderlay(RENDER_CONTEXT context, RENDERER renderer);

    public void prepareRenderMaterial(MaterialVariantId materialId) {
    }

    public abstract ShaderInstance getShaderInstance();

    public static abstract class Tool extends ShaderProvider<RenderQuadContext, IQuadContextRenderer> {

        public abstract void prepareRenderItem(ItemRenderContext context);

        public void preRenderMaterial(ItemRenderContext context, MaterialVariantId materialId) {
        }

        public abstract void startRenderBatch(ItemRenderContext context, STToolRenders.RenderPhase phase);

        public abstract void endRenderBatch(ItemRenderContext context, STToolRenders.RenderPhase phase);

        public void prepareRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }

        public void preRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }
    }

    public static abstract class Armor extends ShaderProvider<RenderArmorPartContext, IArmorPartContextRenderer> {
    }

    public static abstract class Generic extends ShaderProvider<RenderGenericContext, IGenericRenderer> {
    }
}
