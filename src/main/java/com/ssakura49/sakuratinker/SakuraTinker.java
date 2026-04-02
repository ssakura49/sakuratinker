package com.ssakura49.sakuratinker;

import com.ssakura49.sakuratinker.common.tinkering.modules.EnvironmentalAdaptationModule;
import com.ssakura49.sakuratinker.common.tinkering.modules.MultiCurioAttributeModule;
import com.ssakura49.sakuratinker.common.tools.capability.ToolBulletSlotCapability;
import com.ssakura49.sakuratinker.common.tools.tiers.DreadSteelTiers;
import com.ssakura49.sakuratinker.common.tools.tiers.InfinityTiers;
import com.ssakura49.sakuratinker.compat.Botania.init.BotaniaModifier;
import com.ssakura49.sakuratinker.compat.BuddyCards.BuddyCardCompat;
import com.ssakura49.sakuratinker.compat.DreadSteel.DreadSteelCompat;
import com.ssakura49.sakuratinker.compat.EnigmaticLegacy.ELCompat;
import com.ssakura49.sakuratinker.compat.ExtraBotany.init.ExtraBotanyItems;
import com.ssakura49.sakuratinker.compat.ExtraBotany.init.ExtraBotanyModifiers;
import com.ssakura49.sakuratinker.compat.Goety.handler.SpellAttackHandler;
import com.ssakura49.sakuratinker.compat.Goety.init.GoetyItems;
import com.ssakura49.sakuratinker.compat.Goety.init.GoetyModifiers;
import com.ssakura49.sakuratinker.compat.IceAndFireCompat.IAFCompat;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.ISSMaterialStats;
import com.ssakura49.sakuratinker.compat.ReAvaritia.ReAvaritiaCompat;
import com.ssakura49.sakuratinker.compat.TwilightForest.TFCompat;
import com.ssakura49.sakuratinker.compat.YoukaiHomeComing.YKHCCompat;
import com.ssakura49.sakuratinker.data.generator.providiers.STGLMProvider;
import com.ssakura49.sakuratinker.event.event.client.ClientEntityRendererInit;
import com.ssakura49.sakuratinker.event.event.client.ClientGuiRendererInit;
import com.ssakura49.sakuratinker.library.logic.handler.PlayerClickHandler;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.event.SpellBookHandler;
import com.ssakura49.sakuratinker.library.tinkering.tools.STMaterialStats;
import com.ssakura49.sakuratinker.network.PacketHandler;
import com.ssakura49.sakuratinker.register.*;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import com.ssakura49.sakuratinker.utils.time.TimeContext;
import com.ssakura49.sakuratinker.utils.time.TimeStopUtils;
import com.ssakura49.sakuratinker.utils.tinker.ModifierConfigHelper;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.objectweb.asm.Type;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.utils.Util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Mod(SakuraTinker.MODID)
@Mod.EventBusSubscriber(modid = SakuraTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SakuraTinker {
    public static final String MODID = "sakuratinker";
    public static final Logger LOGGER = LogManager.getLogger("sakuratinker");
    public static StringBuilder stringBuilder = new StringBuilder();
    public static boolean modInitialized = false;
    public static ResourceLocation getResource(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }

    public SakuraTinker(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterTiers);
        modEventBus.addListener(this::registerSerializers);
        forgeEventBus.addListener(PlayerClickHandler::onLeftClick);
        forgeEventBus.addListener(PlayerClickHandler::onLeftClickBlock);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, STConfig.Client.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, STConfig.Common.SPEC);
        STItems.ITEMS.register(modEventBus);
        STItems.TINKER_ITEMS.register(modEventBus);
        STGroups.CREATIVE_MODE_TABS.register(modEventBus);
        STBlocks.BLOCKS.register(modEventBus);
        STFluids.FLUIDS.register(modEventBus);
        STEffects.EFFECT.register(modEventBus);
        STModifiers.MODIFIERS.register(modEventBus);
        STEntities.ENTITIES.register(modEventBus);
        STSounds.SOUNDS.register(modEventBus);
        STParticles.PARTICLES.register(modEventBus);
        STAttributes.ATTRIBUTES.register(modEventBus);
        STRecipes.init(modEventBus);
        STSlots.init();
        STTags.init();
        STMenus.MENUS.register(modEventBus);
        STGLMProvider.register(modEventBus);
//        modEventBus.addListener((EntityAttributeCreationEvent e) -> STEntities.registerAttributes((type, builder) -> e.put(type, builder.build())));
//        STMemoryModules.register(modEventBus);

        PacketHandler.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modInitialized = true;
            ClientEntityRendererInit.init();
            ClientGuiRendererInit.init();
        });
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            modInitialized = true;
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (!TimeStopUtils.isTimeStop) {
                    ++TimeContext.Both.timeStopModifyMillis;
                }
            }, 1, 1, TimeUnit.MILLISECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
        });



        if (SafeClassUtil.YHKCLoaded) {
            YKHCCompat.YKHC_MODIFIERS.register(modEventBus);
            LOGGER.info("Found YouKai Home Coming, integration initializing……");
        }
        if (SafeClassUtil.EnigmaticLegacyLoaded) {
            ELCompat.EL_MODIFIERS.register(modEventBus);
            LOGGER.info("Found Enigmatic Legacy, integration initializing……");
        }
        if (SafeClassUtil.ISSLoaded) {
            ISSCompat.ISS_MODIFIERS.register(modEventBus);
            ISSCompat.TINKER_ISS_ITEMS.register(modEventBus);
            SpellBookHandler.init();
            LOGGER.info("[Sakura Tinker]: Found Iron's Spellbooks, integration initializing……");
        }
        if (SafeClassUtil.AvaritiaLoaded) {
            ReAvaritiaCompat.REA_MODIFIERS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Re:Avaritia, integration initializing……");
        }
        if (SafeClassUtil.TFLoaded) {
           TFCompat.TF_MODIFIERS.register(modEventBus);
           LOGGER.info("[Sakura Tinker]: Found Twilight Forest, integration initializing……");
        }
        if (SafeClassUtil.IceAndFireLoaded) {
            IAFCompat.IAF_MODIFIERS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Ice And Fire, integration initializing……");
        }
        if (SafeClassUtil.BotaniaLoaded) {
            BotaniaModifier.BOTANIA_MODIFIERS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Botania, integration initializing……");
        }
        if (SafeClassUtil.ExtraBotanyLoaded) {
            ExtraBotanyModifiers.EXBOT_MODIFIERS.register(modEventBus);
            ExtraBotanyItems.TINKER_ITEMS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Extra Botany, integration initializing……");
        }
        if (SafeClassUtil.DreadSteelLoaded) {
            DreadSteelCompat.DE_MODIFIERS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Dread Steel, integration initializing……");
        }
        if (SafeClassUtil.GoetyLoaded) {
            GoetyItems.register(modEventBus);
            GoetyModifiers.register(modEventBus);
            forgeEventBus.addListener(SpellAttackHandler::onLivingHurt);
            LOGGER.info("[Sakura Tinker]: Found Goety, integration initializing……");
        }
        if (SafeClassUtil.BuddyCardLoaded) {
            BuddyCardCompat.MODIFIERS.register(modEventBus);
            LOGGER.info("[Sakura Tinker]: Found Buddy Card, integration initializing……");
        }
//        if (SafeClassUtil.GoetyRevelationLoaded) {
//            GRModifiers.MODIFIERS.register(modEventBus);
//            GRModifierEventHandler.init();
//            LOGGER.info("[Sakura Tinker]: Found Goety Revelation, integration initializing……");
//        }
    }

    @SubscribeEvent
    public void onRegisterTiers(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            TierSortingRegistry.registerTier(
                    InfinityTiers.instance,
                    ResourceLocation.fromNamespaceAndPath(MODID, "infinity"),
                    List.of(Tiers.NETHERITE),
                    List.of()
            );
            TierSortingRegistry.registerTier(
                    DreadSteelTiers.instance,
                    ResourceLocation.fromNamespaceAndPath(MODID, "dread_steel"),
                    List.of(Tiers.NETHERITE),
                    List.of(InfinityTiers.instance)
            );
        }
    }
    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(STMaterialStats::init);
        //ToolCapabilityProvider.register((stack, tool) -> new ForgeEnergyCapability.Provider(tool));
//        if(ModListUtil.Ember) {
//            ToolCapabilityProvider.register((stack, tool) -> new EmberEnergyCapability.Provider(tool));
//        }
        if (SafeClassUtil.ISSLoaded) {
            event.enqueueWork(ISSMaterialStats::init);
            event.enqueueWork(ISSToolStats::init);
        }
        event.enqueueWork(STConfig::loadOrCreateTreasureConfig);
        ToolCapabilityProvider.register(((stack, tool) -> new ToolBulletSlotCapability.Provider(tool)));
    }
    @SubscribeEvent
    public void registerSerializers(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
            ModifierModule.LOADER.register(getResource("environmental_adaptation"), EnvironmentalAdaptationModule.LOADER);
            ModifierModule.LOADER.register(getResource("multi_curio_attribute"), MultiCurioAttributeModule.LOADER);
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        ModifierConfigHelper.reload();
    }

    public static String makeDescriptionId(String type, String name) {
        return type + ".sakuratinker." + name;
    }

    public static String makeTranslationKey(String base, String name) {
        return Util.makeTranslationKey(base, getResource(name));
    }
    public static MutableComponent makeTranslation(String base, String name) {
        return Component.translatable(makeTranslationKey(base, name));
    }

    public static synchronized void outArray(Object[] o) {
        System.out.println(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + Arrays.toString(o));
    }
    public static synchronized void out(Object o) {
        System.out.println(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + o);
//        SakuraTinkerCore.stream.println(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + o);

    }
    public static synchronized void outV(Vector2f o) {
        System.out.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + "(%.2f, %.2f)", o.x, o.y);
        System.out.print("\n");
//        SakuraTinkerCore.stream.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + "(%.2f, %.2f)", o.x, o.y);
//        SakuraTinkerCore.stream.print("\n");
    }
    public static synchronized void outV(Vector3f o) {
        System.out.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + "(%.2f, %.2f, %.2f)", o.x, o.y, o.z);
        System.out.print("\n");
//        SakuraTinkerCore.stream.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + "(%.2f, %.2f, %.2f)", o.x, o.y, o.z);
//        SakuraTinkerCore.stream.print("\n");
    }
    public static synchronized void out(String text, Object... o) {
        System.out.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + text, o);
        System.out.print("\n");

//        SakuraTinkerCore.stream.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + text, o);
//        SakuraTinkerCore.stream.print("\n");
    }

    public static synchronized void out(Object... o) {
        System.out.print(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]");
        for (Object o1 : o)
            System.out.print(o1 + ", ");
        System.out.print("\n");

//        SakuraTinkerCore.stream.print(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]");
//        for (Object o1 : o)
//            SakuraTinkerCore.stream.print(o1 + ", ");
//        SakuraTinkerCore.stream.print("\n");
    }

    public static synchronized void push() {
        stringBuilder.append(" ");
    }

    public static synchronized void pop() {
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }

    public static synchronized void push2() {
        push();
        push();
    }

    public static synchronized void pop2() {
        pop();
        pop();
    }

    public static String transformClassName(String className) {
        if (className.startsWith("L")) className = className.substring(1);
        return className.replace('/', '.').replace(";", "");
    }

    public static Stream<Type> modClassTypes() {
        return ModList.get().getModFileById(MODID).getFile().getScanResult()
                .getClasses()
                .stream()
                .map(ModFileScanData.ClassData::clazz);
    }

    public static List<String> autoRegNamesInPackage(String packageName) {
        return modClassTypes()
                .filter(type -> type.getDescriptor().startsWith("L" + packageName.replace('.', '/')))
                .map(Type::getClassName).toList();
    }

    public static void sealSakuraTinkerClass(Object self, String base, String solution) {
        String name = self.getClass().getName();
        if (!name.startsWith("com.ssakura49.sakuratinker.")) {
            throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
        }
    }
}
