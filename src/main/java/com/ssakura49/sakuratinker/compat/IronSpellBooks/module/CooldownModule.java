package com.ssakura49.sakuratinker.compat.IronSpellBooks.module;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.CooldownModifierHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record CooldownModule(float percentage, int flat) implements ModifierModule, CooldownModifierHook {
    public static final RecordLoadable<CooldownModule> LOADABLE = RecordLoadable.create(
            FloatLoadable.PERCENT.defaultField("percentage", 0.0f, CooldownModule::percentage), // 0.1 表示减 10%
            IntLoadable.FROM_ZERO.defaultField("flat", 0, CooldownModule::flat),
            CooldownModule::new
    );

    @Override
    public int getCooldown(IToolStackView tool, ModifierEntry modifier, int baseCooldown, int currentCooldown) {
        int level = modifier.getLevel();
        float factor = 1.0f - (percentage * level);
        return (int)(currentCooldown * factor) - (flat * level);
    }

    @Override
    public @NotNull RecordLoadable<? extends ModifierModule> getLoader() {
        return LOADABLE;
    }

    @Override
    public @NotNull List<ModuleHook<?>> getDefaultHooks() {
        return List.of(ISSHooks.COOLDOWN);
    }
}