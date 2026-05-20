package com.ssakura49.sakuratinker.compat.IronSpellBooks.module;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.ManaCostModifierHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record ManaCostModule(float percentage, int flat) implements ModifierModule, ManaCostModifierHook {
    public static final RecordLoadable<ManaCostModule> LOADABLE = RecordLoadable.create(
            FloatLoadable.PERCENT.defaultField("percentage", 0.0f, ManaCostModule::percentage),
            IntLoadable.FROM_ZERO.defaultField("flat", 0, ManaCostModule::flat),
            ManaCostModule::new
    );

    @Override
    public int getManaCost(IToolStackView tool, ModifierEntry modifier, int baseCost, int currentCost) {
        int level = modifier.getLevel();
        float factor = 1.0f - (percentage * level);
        return (int)(currentCost * factor) - (flat * level);
    }

    @Override
    public @NotNull List<ModuleHook<?>> getDefaultHooks() {
        return List.of(ISSHooks.MANA_COST);
    }

    @Override
    public RecordLoadable<? extends ModifierModule> getLoader() {
        return LOADABLE;
    }
}
