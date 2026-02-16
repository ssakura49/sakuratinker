package com.ssakura49.sakuratinker.data.generator.providiers.tinker;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.data.generator.STTagKeys;
import com.ssakura49.sakuratinker.register.STFluids;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider;
import slimeknights.mantle.registration.object.FluidObject;

public class STFluidContainerTransferProvider extends AbstractFluidContainerTransferProvider implements IConditionBuilder {
    public STFluidContainerTransferProvider(PackOutput packOutput) {
        super(packOutput, SakuraTinker.MODID);
    }

    @Override
    protected void addTransfers() {
        addFillEmpty(
                "ice_dragon_blood_",
                IafItemRegistry.ICE_DRAGON_BLOOD.get(),
                Items.GLASS_BOTTLE,
                STTagKeys.Fluids.molten_ice_dragon_blood,
                250,
                false,
                modLoaded(SafeClassUtil.Modid.IceAndFire)
        );
        addFillEmpty(
                "fire_dragon_blood_",
                IafItemRegistry.FIRE_DRAGON_BLOOD.get(),
                Items.GLASS_BOTTLE,
                STTagKeys.Fluids.molten_fire_dragon_blood,
                250,
                false,
                modLoaded(SafeClassUtil.Modid.IceAndFire)
        );
        addFillEmpty(
                "lightning_dragon_blood_",
                IafItemRegistry.LIGHTNING_DRAGON_BLOOD.get(),
                Items.GLASS_BOTTLE,
                STTagKeys.Fluids.molten_lightning_dragon_blood,
                250,
                false,
                modLoaded(SafeClassUtil.Modid.IceAndFire)
        );
    }

    @Override
    public @NotNull String getName() {
        return "Sakura Tinker Fluid Container Transfer Provider";
    }
}
