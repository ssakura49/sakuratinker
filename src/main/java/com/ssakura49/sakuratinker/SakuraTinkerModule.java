package com.ssakura49.sakuratinker;

import com.ssakura49.sakuratinker.common.items.SakuraTinkerItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.BlockEntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.EnumDeferredRegister;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;

public abstract class SakuraTinkerModule {
    protected static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String name) {
        return ResourceKey.create(registry, new ResourceLocation(SakuraTinker.MODID, name));
    }

    protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(SakuraTinker.MODID);
    protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(SakuraTinker.MODID);
    protected static final FluidDeferredRegister FLUID = new FluidDeferredRegister(SakuraTinker.MODID);
    protected static final EnumDeferredRegister<MobEffect> MOB_EFFECTS = new EnumDeferredRegister<>(Registries.MOB_EFFECT, SakuraTinker.MODID);
    //gameplay instance
    protected static final BlockEntityTypeDeferredRegister BLOCK_ENTITIES = new BlockEntityTypeDeferredRegister(SakuraTinker.MODID);
    protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(SakuraTinker.MODID);
    //datapacks
    protected static final SynchronizedDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = SynchronizedDeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SakuraTinker.MODID);
    public static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TAB = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, SakuraTinker.MODID);
    public static final RegistryObject<CreativeModeTab> tab = CREATIVE_TAB.register(
            "sakuratinker", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "sakuratinker"))
                    .icon(() -> new ItemStack(TinkerSmeltery.smelteryController))
                    .displayItems(SakuraTinkerItems::addTabItems)
                    .withTabsBefore(TinkerToolParts.tabToolParts.getId())
                    .build());

    public static void initRegisters() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID.register(bus);
        MOB_EFFECTS.register(bus);
        ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }



    protected static ResourceLocation resource(String path) {
        return SakuraTinker.getResource(path);
    }
}
