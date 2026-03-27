package com.ssakura49.sakuratinker.common.tinkering.modifiers.melee.soulDevourerModifier;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.library.hooks.combat.GenericCombatModifierHook;
import com.ssakura49.sakuratinker.library.tinkering.tools.STHooks;
import com.ssakura49.sakuratinker.utils.CommonRGBUtil;
import com.ssakura49.sakuratinker.utils.component.DynamicComponentUtil;
import com.ssakura49.sakuratinker.utils.tinker.ToolUtil;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageTargetPreModifierHook;
import com.ssakura49.tinkercuriolib.hook.combat.CurioKillTargetModifierHook;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class SoulDevourerModifier extends Modifier implements
        CurioKillTargetModifierHook,
        GenericCombatModifierHook,
        MeleeDamageModifierHook,
        TooltipModifierHook,
        CurioDamageTargetPreModifierHook,
        ModifierRemovalHook
{
    private static final ResourceLocation SOUL_KILL_COUNT =
            SakuraTinker.getResource("soul_devourer_kills");
    private static final float BONUS_PER_KILL = 0.0001f;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,
                TCLibHooks.CURIO_KILL_TARGET,
                STHooks.GENERIC_COMBAT,
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.TOOLTIP,
                TCLibHooks.CURIO_DAMAGE_TARGET_PRE,
                ModifierHooks.REMOVE
                );
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void onKillLivingTarget(IToolStackView tool, ModifierEntry entry, LivingDeathEvent event, LivingEntity attacker, LivingEntity target) {
        if (event.getSource().getEntity() == attacker) {
            float currentBonus = tool.getPersistentData().getFloat(SOUL_KILL_COUNT);
            tool.getPersistentData().putFloat(SOUL_KILL_COUNT, currentBonus + BONUS_PER_KILL * entry.getLevel());
        }
    }

    @Override
    public void onCurioToKillTarget(IToolStackView curio, ModifierEntry entry, LivingDeathEvent event, LivingEntity attacker, LivingEntity target) {
        if (event.getSource().getEntity() == attacker) {
            float currentBonus = curio.getPersistentData().getFloat(SOUL_KILL_COUNT);
            curio.getPersistentData().putFloat(SOUL_KILL_COUNT, currentBonus + BONUS_PER_KILL * entry.getLevel());
        }
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float actualDamage) {
        int kills = tool.getPersistentData().getInt(SOUL_KILL_COUNT);
        int level = modifier.getLevel();
        float bonus = SoulDevourerFormula.current().computeBonus(kills, level);
        return actualDamage * (1.0f + bonus);
    }

    @Override
    public void onDamageTargetPre(IToolStackView curio, ModifierEntry entry, LivingHurtEvent event, LivingEntity attacker, LivingEntity target) {
        if (attacker!=null&& !ToolUtil.hasModifierInHeldTool(attacker,this)) {
            int kills = curio.getPersistentData().getInt(SOUL_KILL_COUNT);
            float bonus = SoulDevourerFormula.current().computeBonus(kills, entry.getLevel());
            float damage = event.getAmount() * (1.0f + bonus);
            event.setAmount(damage);
        }
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(SOUL_KILL_COUNT);
        return null;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifierEntry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        float bonus = tool.getPersistentData().getFloat(SOUL_KILL_COUNT) * 100;
        int level = modifierEntry.getLevel();
        String text = I18n.get("modifier.sakuratinker.soul_devourer.tooltip") + ": " + String.format("%.2f", bonus) + "("+ level + ")";

        tooltip.add(DynamicComponentUtil.WaveColorText.getColorfulText(
                text,
                null,
                CommonRGBUtil.darkBlue.getRGB(),
                40,
                1000,
                true
        ));
    }
}
