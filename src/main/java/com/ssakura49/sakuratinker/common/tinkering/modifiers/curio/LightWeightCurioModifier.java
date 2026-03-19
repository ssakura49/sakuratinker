package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.mining.CurioBreakSpeedModifierHook;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public class LightWeightCurioModifier extends Modifier implements CurioBreakSpeedModifierHook, ToolStatsModifierHook {
    public LightWeightCurioModifier(){}
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_BREAK_SPEED, ModifierHooks.TOOL_STATS);
    }
    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        STToolStats.MOVEMENT_SPEED.add(builder, (double)(0.05F * (float)modifier.getLevel()));
    }

    @Override
    public void onCurioBreakSpeed(IToolStackView curio, ModifierEntry entry, PlayerEvent.BreakSpeed event, Player player) {
        event.setNewSpeed(event.getNewSpeed() * (1.0F + 0.07F * entry.getLevel()));
    }
}
