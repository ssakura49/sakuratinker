package com.ssakura49.sakuratinker;

import com.ssakura49.sakuratinker.common.intergration.*;
import com.ssakura49.sakuratinker.common.register.MaterialRegister;
import com.ssakura49.sakuratinker.common.register.EffectsRegister;
import com.ssakura49.sakuratinker.common.fluids.SakuraTinkerFluids;
import com.ssakura49.sakuratinker.common.items.SakuraTinkerItems;
import com.ssakura49.sakuratinker.common.register.ModifiersRegister;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import slimeknights.tconstruct.library.utils.Util;

@Mod(MaterialRegister.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SakuraTinker {
    public static String MODID = "sakuratinker";
    public static final Logger LOGGER = LogManager.getLogger("sakuratinker");

    public SakuraTinker() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        SakuraTinkerItems.ITEMS.register(bus);
        SakuraTinkerModule.CREATIVE_TAB.register(bus);
        SakuraTinkerFluids.FLUIDS.register(bus);
        EffectsRegister.EFFECT.register(bus);
        ModifiersRegister.MODIFIERS.register(bus);
        SakuraTinkerModule.initRegisters();

        if (ModList.get().isLoaded("youkaishomecoming")) {
            YKHCIntergration.Init();
            LOGGER.info("Found YouKai Home Coming, integration initializing……");
        }
        if (ModList.get().isLoaded("enigmaticlegacy")) {
            ELIntergration.Init();
            LOGGER.info("Found Enigmatic Legacy, integration initializing……");
        }
        if (ModList.get().isLoaded("irons_spellbooks")) {
            ISSIntergration.Init();
            LOGGER.info("Found Iron's Spellbooks, integration initializing……");
        }
        if (ModList.get().isLoaded("avaritia")) {
            ReAvaritiaIntergration.Init();
            LOGGER.info("Found Re:Avaritia, integration initializing……");
        }
        if (ModList.get().isLoaded("twilightforest")) {
            TFIntergration.Init();
            LOGGER.info("Found Twilight Forest, integration initializing……");
        }
    }

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {}
        if (event.includeServer()) {}
    }

    public static String makeTranslationKey(String base, String name) {
        return Util.makeTranslationKey(base, getResource(name));
    }
    public static MutableComponent makeTranslation(String base, String name) {
        return Component.translatable(makeTranslationKey(base, name));
    }
    public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
        return Component.translatable(makeTranslationKey(base, name), arguments);
    }
    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(MODID, name);
    }

}