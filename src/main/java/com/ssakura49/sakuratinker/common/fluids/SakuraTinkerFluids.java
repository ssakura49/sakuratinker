package com.ssakura49.sakuratinker.common.fluids;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.TConstruct;

public class SakuraTinkerFluids {
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(SakuraTinker.MODID);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_test = register("molten_test", 2500);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_youkai = register("molten_youkai", 2400);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_etherium = register("molten_etherium", 2500);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_arcane_salvage = register("molten_arcane_salvage", 2500);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_infinity = register("molten_infinity", 6000);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_soul_sakura = register("molten_soul_sakura", 3200);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_knightmetal = register("molten_knightmetal", 1000);
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_fiery = register("molten_fiery", 1700);

    private static FluidType.Properties hot(String name) {
        return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000).descriptionId(TConstruct.makeDescriptionId("fluid", name)).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
    }
    private static FluidType.Properties cool() {
        return FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
    }
    private static FlowingFluidObject<ForgeFlowingFluid> register(String name, int temp) {
        return FLUIDS.register(name).type(hot(name).temperature(temp).lightLevel(12)).block(MapColor.COLOR_RED, 12).bucket().flowing();
    }
}
