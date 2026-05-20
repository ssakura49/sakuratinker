package com.ssakura49.sakuratinker.compat.IronSpellBooks;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.*;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

public class ISSHooks {
    public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Modifier Hook");

    public static final ModuleHook<SpellDamageModifierHook> SPELL_DAMAGE = ModifierHooks.register(
            SakuraTinker.getResource("spell_damage"),
            SpellDamageModifierHook.class,
            SpellDamageModifierHook.AllMerger::new,
            (tool, modifier, context, baseDamage, damage)->damage
    );

    public static final ModuleHook<SpellHitModifierHook> SPELL_HIT = ModifierHooks.register(
            SakuraTinker.getResource("spell_hit"),
            SpellHitModifierHook.class,
            SpellHitModifierHook.AllMerger::new,
            new SpellHitModifierHook() {}
    );

    public static final ModuleHook<CooldownModifierHook> COOLDOWN = ModifierHooks.register(
            SakuraTinker.getResource("spell_cooldown"),
            CooldownModifierHook.class,
            CooldownModifierHook.AllMerger::new,
            ((tool, modifier, baseCooldown, currentCooldown) -> currentCooldown)
    );

    public static final ModuleHook<InscribeSpellModifierHook> INSCRIBE_SPELL = ModifierHooks.register(
            SakuraTinker.getResource("inscribe_spell"),
            InscribeSpellModifierHook.class,
            InscribeSpellModifierHook.AllMerger::new,
            new InscribeSpellModifierHook() {}
    );

    public static final ModuleHook<ManaCostModifierHook> MANA_COST = ModifierHooks.register(
            SakuraTinker.getResource("mana_cost"),
            ManaCostModifierHook.class,
            ManaCostModifierHook.AllMerger::new,
            ((tool, modifier, baseCost, currentCost) -> currentCost)
    );

    public static final ModuleHook<SpellCastModifierHook> PRE_SPELL_CAST = ModifierHooks.register(
            SakuraTinker.getResource("pre_spell_cast"),
            SpellCastModifierHook.class,
            SpellCastModifierHook.AllMerger::new,
            new SpellCastModifierHook() {}
    );

    public static final ModuleHook<SpellCastModifierHook> ON_SPELL_CAST = ModifierHooks.register(
            SakuraTinker.getResource("on_spell_cast"),
            SpellCastModifierHook.class,
            SpellCastModifierHook.AllMerger::new,
            new SpellCastModifierHook() {}
    );


    public static final ModuleHook<SpellHealModifierHook> SPELL_HEAL = ModifierHooks.register(
            SakuraTinker.getResource("spell_heal"),
            SpellHealModifierHook.class,
            SpellHealModifierHook.AllMerger::new,
            new SpellHealModifierHook() {}
    );

    public static final ModuleHook<SpellLevelModifierHook> SPELL_LEVEL = ModifierHooks.register(
            SakuraTinker.getResource("spell_level"),
            SpellLevelModifierHook.class,
            SpellLevelModifierHook.AllMerger::new,
            ((tool, modifier,context, baseLevel, currentLevel) -> currentLevel)
    );

    public static final ModuleHook<SpellSchoolModifierHook> SPELL_SCHOOL = ModifierHooks.register(
           SakuraTinker.getResource("spell_school"),
            SpellSchoolModifierHook.class,
            SpellSchoolModifierHook.AllMerger::new,
            ((tool, modifier, spell, currentSchool) -> currentSchool)
    );

    public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
        return LOADER.register(new ModuleHook<>(name, filter, merger, defaultInstance));
    }
}
