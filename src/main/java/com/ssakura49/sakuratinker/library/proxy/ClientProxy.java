package com.ssakura49.sakuratinker.library.proxy;

import com.ssakura49.sakuratinker.render.RendererUtils;
import com.ssakura49.sakuratinker.utils.time.TimeContext;
import com.ssakura49.sakuratinker.utils.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientProxy implements ModProxy{
    public static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    public final Lock LOCK = new ReentrantLock();

    public ClientProxy() {
        LOCK.lock();
        try {
            Minecraft mc = Minecraft.getInstance();
            SERVICE.scheduleAtFixedRate(() -> {
                TimeContext.Client.count++;
                if (TimeContext.Client.timeStopGLFW == 0L)
                    TimeContext.Client.timeStopGLFW = (long) (GLFW.glfwGetTime() * 1000L);

                if (!TimeStopUtils.isTimeStop || !RendererUtils.isTimeStop_andSameDimension) {
                    ++TimeContext.Both.timeStopModifyMillis;
                    if (!mc.isPaused()) TimeContext.Client.timeStopGLFW++;
                }
            }, 0L, 1L, TimeUnit.MILLISECONDS);

//            ReloadableResourceManager manager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
//            manager.registerReloadListener(PostProcessingShaders.INSTANCE);
//            PostEffectHandler.registerEffect(ModernGaussianBlurPostEffect::new);
        } finally {
            LOCK.unlock();
        }
//        IEventBus modBus = EndingLibrary.getModEventBus();
//        modBus.addListener(this::clientSetup);
    }

//    public void clientSetup(final FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            MenuScreens.register(ModMenus.OTHER_PLAYER_INV_MENU.get(), OtherPlayerInventoryScreen::new);
//        });
//    }
}
