package com.ssakura49.sakuratinker.proxy;

import com.ssakura49.sakuratinker.common.capability.CapabilityRegistry;
import com.ssakura49.sakuratinker.common.capability.entity.PlayerCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy implements ModProxy {
    public static LazyOptional<PlayerCapability> getPlayerCapOptional(Player player) {
        return player.getCapability(CapabilityRegistry.PLAYER_CAP);
    }
}
