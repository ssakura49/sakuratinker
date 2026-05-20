package com.ssakura49.sakuratinker.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ssakura49.sakuratinker.client.render.provider.context.ItemRenderContext;
import com.ssakura49.sakuratinker.client.render.provider.context.tool.RenderQuadContext;
import com.ssakura49.sakuratinker.client.render.provider.renderer.IQuadContextRenderer;
import com.ssakura49.sakuratinker.client.render.shader.ShaderProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public class STRenderTasks {
    public static abstract class RenderTask {
        protected final STToolRenders.RenderPhase phase;

        protected RenderTask(STToolRenders.RenderPhase phase) {
            this.phase = phase;
        }

        public abstract void applyRenderTask();

        public STToolRenders.RenderPhase getPhase() {
            return phase;
        }
    }

    public static class InstantRenderTask extends RenderTask {
        private final Runnable renderingFunc;

        public InstantRenderTask(STToolRenders.RenderPhase phase, Runnable renderingFunc) {
            super(phase);
            this.renderingFunc = renderingFunc;
        }

        @Override
        public void applyRenderTask() {
            renderingFunc.run();
        }
    }

    public static class NakedRenderTask extends RenderTask {
        private final RenderQuadContext context;
        private final IQuadContextRenderer renderer;

        public NakedRenderTask(STToolRenders.RenderPhase phase, RenderQuadContext context, IQuadContextRenderer renderer) {
            super(phase);
            this.context = context;
            this.renderer = renderer;
        }

        @Override
        public void applyRenderTask() {
            renderer.render(
                    context.renderContext(),
                    context.quad(),
                    context.getConsumer()
            );
        }

        public void applyRenderTask(VertexConsumer consumer) {
            renderer.render(
                    context.renderContext(),
                    context.quad(),
                    consumer
            );
        }
    }

    public static class NakedBatchTask extends RenderBatchTask {
        private final ItemRenderContext itemRenderContext;
        private final RenderType renderType;

        public NakedBatchTask(STToolRenders.ToolRenderBatch renderBatch, ItemRenderContext itemRenderContext, RenderType renderType) {
            super(renderBatch, itemRenderContext);
            this.itemRenderContext = itemRenderContext;
            this.renderType = renderType;
        }

        @Override
        public void applyRenderTask() {
            MultiBufferSource bufferSource = itemRenderContext.bufferSource();
            VertexConsumer buffer = bufferSource.getBuffer(renderType);

            for (RenderTask renderTask : renderBatch.renderTasks()) {
                if (renderTask instanceof NakedRenderTask nakedRenderTask) {
                    nakedRenderTask.applyRenderTask(buffer);
                }
            }
        }
    }

    public static class RenderBatchTask extends RenderTask {
        protected final STToolRenders.ToolRenderBatch renderBatch;
        private final ItemRenderContext itemRenderContext;

        public RenderBatchTask(STToolRenders.ToolRenderBatch renderBatch, ItemRenderContext itemRenderContext) {
            super(renderBatch.renderPhase());
            this.renderBatch = renderBatch;
            this.itemRenderContext = itemRenderContext;
        }

        @Override
        public void applyRenderTask() {
            ShaderProvider.Tool shaderProvider = renderBatch.shaderProvider();
            List<RenderTask> renderTasks = renderBatch.renderTasks();

            if (shaderProvider != null) {
                shaderProvider.startRenderBatch(itemRenderContext, renderBatch.renderPhase());
            }

            for (RenderTask renderTask : renderTasks) {
                renderTask.applyRenderTask();
            }

            if (shaderProvider != null) {
                shaderProvider.endRenderBatch(itemRenderContext, renderBatch.renderPhase());
            }
        }
    }
}
