package com.ssakura49.sakuratinker.client.render.provider.context.tool;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ssakura49.sakuratinker.client.render.provider.context.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;

public record RenderQuadContext(
        ItemStack itemStack,
        RenderType renderType,
        RenderContext renderContext,
        BakedQuad quad
) {
    public VertexConsumer getConsumer() {
        return renderContext.getBuffer(renderType);
    }
}
