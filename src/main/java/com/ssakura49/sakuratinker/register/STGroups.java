package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.ssakura49.sakuratinker.compat.ExtraBotany.init.ExtraBotanyItems.*;
import static com.ssakura49.sakuratinker.compat.Goety.init.GoetyItems.*;
import static com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat.*;
import static com.ssakura49.sakuratinker.register.STItems.*;

public class STGroups {
//    public static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TAB = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, SakuraTinker.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SakuraTinker.MODID);

    private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
        ToolBuildHandler.addVariants(output, tool.get(), "");
    }
    private static void acceptTools(Consumer<ItemStack> output, EnumObject<?, ? extends IModifiable> tools) {
        tools.forEach((tool) -> ToolBuildHandler.addVariants(output, tool, ""));
    }
    private static void acceptPart(Consumer<ItemStack> output, Supplier<? extends IMaterialItem> item) {
        item.get().addVariants(output, "");
    }
    private static void accept(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter, CastItemObject cast) {
        output.accept((ItemLike)getter.apply(cast));
    }


    public STGroups(){
    }
    public static final RegistryObject<CreativeModeTab> ITEMS_TAB = CREATIVE_MODE_TABS.register("st_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sakuratinker.st_items"))
            .icon(() -> fox_mask.get().getDefaultInstance())
            .withTabsBefore(TinkerToolParts.tabToolParts.getId())
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> object : LIST_COMMON_ITEMS) {
                    if (object.isPresent()) {
                        output.accept(object.get());
                    }
                }
            })
            .build());

    public static final RegistryObject<CreativeModeTab> MATERIAL_TAB = CREATIVE_MODE_TABS.register("st_material", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sakuratinker.st_material"))
            .icon(() -> soul_sakura.get().getDefaultInstance())
            .withTabsBefore(TinkerToolParts.tabToolParts.getId())
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> object : LIST_MATERIAL) {
                    if (object.isPresent()) {
                        output.accept(object.get());
                    }
                }
            })
            .build());
    public static final RegistryObject<CreativeModeTab> BLOCK_TAB = CREATIVE_MODE_TABS.register("st_block", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sakuratinker.st_block"))
            .icon(() -> eezo_ore.get().getDefaultInstance())
            .withTabsBefore(TinkerToolParts.tabToolParts.getId())
            .displayItems((parameters, output) -> {
                for (RegistryObject<BlockItem> object : LIST_SIMPLE_BLOCK) {
                    if (object.isPresent()) {
                        output.accept(object.get());
                    }
                }
            })
            .build());
    public static final RegistryObject<CreativeModeTab> TOOL_TAB = CREATIVE_MODE_TABS.register("st_tools", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sakuratinker.st_tools"))
            .icon(() -> ((ModifiableItem)swift_sword.get()).getRenderTool())
            .withTabsBefore(TinkerToolParts.tabToolParts.getId())
            .displayItems(STGroups::addToolItems)
            .build());

    private static void addToolItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        Objects.requireNonNull(output);
        Consumer<ItemStack> outputTool = output::accept;
        Consumer<ItemStack> outputPart = output::accept;
        acceptPart(outputPart, charm_chain);
        acceptPart(outputPart, charm_core);
        acceptPart(outputPart, swift_blade);
        acceptPart(outputPart, swift_guard);
        acceptPart(outputPart, barrel);
        acceptPart(outputPart, laser_medium);
        acceptPart(outputPart, energy_unit);
        acceptPart(outputPart, blade);
        acceptPart(outputPart, arrow_head);
        acceptPart(outputPart, arrow_shaft);
        acceptPart(outputPart, fletching);
        acceptPart(outputPart, blade_box);
        if (SafeClassUtil.ISSLoaded) {
            acceptPart(outputPart, book_cover);
            acceptPart(outputPart, spell_cloth);
        }
        acceptPart(outputPart, great_blade);
        acceptPart(outputPart, shell);
        acceptPart(outputPart, flag);
        if (SafeClassUtil.ExtraBotanyLoaded) {
            acceptPart(outputPart, phantom_core);
        }
        acceptPart(outputPart, axle);
        acceptPart(outputPart, yoyo_body);
        acceptPart(outputPart, yoyo_ring);
        acceptPart(outputPart, chord);
        acceptPart(outputPart, scythe_blade);
        if (SafeClassUtil.GoetyLoaded) {
            acceptPart(outputPart, soul_gatherer);
        }
        acceptPart(outputPart, range_core);

        acceptTool(outputTool, tinker_charm);
        acceptTool(outputTool, great_sword);
        acceptTool(outputTool, swift_sword);
        acceptTool(outputTool, vampire_knife);
        acceptTool(outputTool, blade_convergence);
        acceptTool(outputTool, laser_gun);
        acceptTool(outputTool, shuriken);
        acceptTool(outputTool, tinker_arrow);
        if (SafeClassUtil.ISSLoaded) {
            acceptTool(outputTool, tinker_spell_book);
        }
        acceptTool(outputTool, grappling_hook);
        acceptTool(outputTool, power_bank);
        acceptTool(outputTool, battle_flag);
        if (SafeClassUtil.ExtraBotanyLoaded) {
            acceptTool(outputTool, first_fractal);
        }
        acceptTool(outputTool, yoyo);
        acceptTool(outputTool, scythe);
        if (SafeClassUtil.GoetyLoaded) {
            acceptTool(outputTool, tinker_wand);
        }
        acceptTool(outputTool, tinker_bullet);
        acceptTool(outputTool, revolver);

        acceptTools(outputTool, embeddedArmor);

        addCasts(output, ItemObject::get);
        addCasts(output, CastItemObject::getSand);
        addCasts(output, CastItemObject::getRedSand);
    }
    private static void addCasts(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter) {
        accept(output, getter, charmChainCast);
        accept(output, getter, charmCoreCast);
        accept(output, getter, swiftBladeCast);
        accept(output, getter, swiftGuardCast);
        accept(output, getter, barrelCast);
        accept(output, getter, energyUnitCast);
        accept(output, getter, laserMediumCast);
        accept(output, getter, bladeCast);
        accept(output, getter, arrowShaftCast);
        accept(output, getter, arrowHeadCast);
        if (SafeClassUtil.ISSLoaded) {
            accept(output, getter, bookCoverCast);
            accept(output, getter, spellClothCast);
        }
        accept(output, getter, greatBladeCast);
        accept(output, getter, flagCast);
        accept(output, getter, shellCast);
        if (SafeClassUtil.ExtraBotanyLoaded) {
            accept(output, getter, phantomCoreCast);
        }

        accept(output, getter, axleCast);
        accept(output, getter, yoyoBodyCast);
        accept(output, getter, yoyoRingCast);
        accept(output, getter, chordCast);
        accept(output, getter, scytheBladeCast);
        if (SafeClassUtil.GoetyLoaded) {
            accept(output, getter, soulGathererCast);
        }
        accept(output, getter, rangeCoreCast);
    }
}
