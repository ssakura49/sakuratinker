package com.ssakura49.sakuratinker.common.tinkering.modifiers.compat;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.data.generator.STMaterialId;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

public class UmbralModifier extends BaseModifier implements ModifierTraitHook {
    private static final MaterialId ID = STMaterialId.echo_slimesteel;
    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, ModifierHooks.MODIFIER_TRAITS);
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }

    @Override
    public void addTraits(IToolContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstAppearance) {
        if (!SafeClassUtil.ISSLoaded) {
            return;
        }
        MaterialNBT materials = context.getMaterials();
        boolean hasMaterial = false;
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant variantId = materials.get(i);
            if (ID.equals(variantId.getId())) {
                hasMaterial = true;
                break;
            }
        }
        if (hasMaterial) {
            builder.add(new ModifierEntry(ISSCompat.UmbralISSModifier.get(),1));
        }
    }
}
