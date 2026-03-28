package com.ssakura49.sakuratinker.compat.IronSpellBooks.item;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.definitions.ISSToolDefinitions;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.item.base.ModifiableSpellBookItem;
import com.ssakura49.sakuratinker.utils.tinker.TooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class TinkerSpellBook extends ModifiableSpellBookItem {
    private int maxSpellSlots;
    public TinkerSpellBook(int maxSpellSlots ,Item.Properties properties) {
        super(1 ,properties, ISSToolDefinitions.TINKER_SPELL_BOOK);
        this.maxSpellSlots = maxSpellSlots;
    }

    public void setMaxSpellSlots(int maxSpellSlots) {
        this.maxSpellSlots = maxSpellSlots;
    }

    @Override
    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
        builder.add(ToolStats.ATTACK_DAMAGE);
        TooltipUtil.addToolStatTooltip(builder, tool, STToolStats.BASE_SPELL_DAMAGE);
        TooltipUtil.addPerToolStatTooltip(builder, tool, STToolStats.SPELL_POWER);
        TooltipUtil.addPerToolStatTooltip(builder, tool, STToolStats.SPELL_REDUCE);
        TooltipUtil.addPerToolStatTooltip(builder, tool, STToolStats.CAST_TIME);
        builder.addAllFreeSlots();

        for(ModifierEntry entry : tool.getModifierList()) {
            ((TooltipModifierHook)entry.getHook(ModifierHooks.TOOLTIP)).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
        }

        return tooltips;
    }
}
