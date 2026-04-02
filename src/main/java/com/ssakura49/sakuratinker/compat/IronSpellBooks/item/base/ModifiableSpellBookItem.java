package com.ssakura49.sakuratinker.compat.IronSpellBooks.item.base;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSToolStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.stat.SchoolToolStat;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.behavior.CurioAttributeModifierHook;
import com.ssakura49.tinkercuriolib.hook.interation.CurioInventoryTickModifierHook;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ModifiableSpellBookItem extends SpellBook implements IModifiableDisplay, ICurioItem {
    private final ToolDefinition toolDefinition;
    private ItemStack toolForRendering;
    private final int maxStackSize;
    protected final int maxSpellSlots;

    public ModifiableSpellBookItem(int maxSpellSlots,Item.Properties properties, ToolDefinition toolDefinition) {
        this(maxSpellSlots ,properties, toolDefinition ,1);
    }

    public ModifiableSpellBookItem(int maxSpellSlots, Item.Properties properties,ToolDefinition toolDefinition, int maxStackSize) {
        super(maxSpellSlots, new Item.Properties().stacksTo(maxStackSize));
        this.toolDefinition = toolDefinition;
        this.maxSpellSlots = maxSpellSlots;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getMaxSpellSlots() {
        return this.maxSpellSlots;
    }

    @Override
    public boolean isUnique() {
        return false;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Level context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
        if (this.isUnique()) {
            lines.add(Component.translatable("tooltip.irons_spellbooks.spellbook_rarity", new Object[]{Component.translatable("tooltip.irons_spellbooks.spellbook_unique").withStyle(TooltipsUtils.UNIQUE_STYLE)}).withStyle(ChatFormatting.GRAY));
        }

        Player player = MinecraftInstanceHelper.getPlayer();
        if (player != null && ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellList = ISpellContainer.get(itemStack);
            lines.add(Component.translatable("tooltip.irons_spellbooks.spellbook_spell_count", new Object[]{spellList.getMaxSpellCount()}).withStyle(ChatFormatting.GRAY));
            List<SpellSlot> activeSpellSlots = spellList.getActiveSpells();
            if (!activeSpellSlots.isEmpty()) {
                lines.add(Component.empty());
                lines.add(Component.translatable("tooltip.irons_spellbooks.press_to_cast", new Object[]{Component.keybind("key.irons_spellbooks.spellbook_cast")}).withStyle(ChatFormatting.GOLD));
                lines.add(Component.empty());
                lines.add(Component.translatable("tooltip.irons_spellbooks.spellbook_tooltip").withStyle(ChatFormatting.GRAY));
                SpellSelectionManager spellSelectionManager = ClientMagicData.getSpellSelectionManager();

                for(int i = 0; i < activeSpellSlots.size(); ++i) {
                    MutableComponent spellText = TooltipsUtils.getTitleComponent(((SpellSlot)activeSpellSlots.get(i)).spellData(), (LocalPlayer)player).setStyle(Style.EMPTY);
                    SpellSelectionManager.SelectionOption option = spellSelectionManager.getSpellSlot(spellSelectionManager.getSelectionIndex());
                    if (MinecraftInstanceHelper.getPlayer() != null && Utils.getPlayerSpellbookStack(MinecraftInstanceHelper.getPlayer()) == itemStack && option != null && option.slot.equals(Curios.SPELLBOOK_SLOT) && option.slotIndex == i) {
                        List<MutableComponent> shiftMessage = TooltipsUtils.formatActiveSpellTooltip(itemStack, spellSelectionManager.getSelectedSpellData(), CastSource.SPELLBOOK, (LocalPlayer)player);
                        shiftMessage.remove(0);
                        TooltipsUtils.addShiftTooltip(lines, Component.literal("> ").append(spellText).withStyle(ChatFormatting.YELLOW), (List)shiftMessage.stream().map((component) -> Component.literal(" ").append(component)).collect(Collectors.toList()));
                    } else {
                        lines.add(Component.literal(" ").append(spellText.withStyle(Style.EMPTY.withColor(8947966))));
                    }
                }
            }
        }
        TooltipUtil.addInformation(this, itemStack, context, lines, SafeClientAccess.getTooltipKey(), flag);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : this.maxStackSize;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EnchantmentModifierHook.getAllEnchantments(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        this.initializeSpellContainer(stack);
        return new ToolCapabilityProvider(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ToolStack tool = ToolStack.from(stack);
        for (ModifierEntry entry : tool.getModifierList()) {
            CurioInventoryTickModifierHook hook = entry.getHook(TCLibHooks.CURIO_TICK);
            hook.onCurioTick(tool, entry, slotContext, slotContext.entity(), stack);
        }
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull IToolStackView tool, @NotNull EquipmentSlot slot) {
        return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null ? this.getAttributeModifiers((IToolStackView) ToolStack.from(stack), (EquipmentSlot)slot) : ImmutableMultimap.of();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        ToolStack toolStack = ToolStack.from(stack);
        StatsNBT toolStats = toolStack.getStats();

        if (SafeClassUtil.ISSLoaded) {
            map.put(AttributeRegistry.MAX_MANA.get(),
                    new AttributeModifier(uuid, "max_mana_bonus",
                            (double) (Float) toolStats.get(ISSToolStats.MANA_VALUE),
                            AttributeModifier.Operation.ADDITION));
            map.put(AttributeRegistry.MANA_REGEN.get(),
                    new AttributeModifier(uuid, "mana_regen_bonus",
                            (double) (Float) toolStats.get(ISSToolStats.MANA_REGEN),
                            AttributeModifier.Operation.MULTIPLY_TOTAL));
            map.put(AttributeRegistry.CAST_TIME_REDUCTION.get(),
                    new AttributeModifier(uuid, "cast_time_reduce_bonus",
                            (double) (Float) toolStats.get(ISSToolStats.CAST_TIME_REDUCE),
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

            for (var entry : ISSToolStats.SCHOOL_BONUS.entrySet()) {
                SchoolType school = entry.getKey();
                FloatToolStat stat = entry.getValue();
                float bonus = toolStats.get(stat);
                if (school == null || bonus == 0f) continue;

                Attribute spellPower = getSpellPowerForSchool(school);
                Attribute magicResist = getMagicResistForSchool(school);

                if (spellPower != null) {
                    map.put(spellPower,
                            new AttributeModifier(uuid,
                                    school.getId().getPath() + "_power_bonus",
                                    bonus,
                                    AttributeModifier.Operation.ADDITION));
                }
                if (magicResist != null) {
                    map.put(magicResist,
                            new AttributeModifier(uuid,
                                    school.getId().getPath() + "_resist_bonus",
                                    bonus,
                                    AttributeModifier.Operation.ADDITION));
                }
            }
        }
        for (ModifierEntry entry : toolStack.getModifierList()) {
            CurioAttributeModifierHook hook = (CurioAttributeModifierHook) entry.getHook(TCLibHooks.CURIO_ATTRIBUTE);
            hook.modifyCurioAttribute(toolStack, entry, slotContext, uuid, map::put);
        }

        return map;
    }

    private Attribute getSpellPowerForSchool(SchoolType school) {
        if (school == SchoolRegistry.FIRE.get()) return AttributeRegistry.FIRE_SPELL_POWER.get();
        if (school == SchoolRegistry.ICE.get()) return AttributeRegistry.ICE_SPELL_POWER.get();
        if (school == SchoolRegistry.LIGHTNING.get()) return AttributeRegistry.LIGHTNING_SPELL_POWER.get();
        if (school == SchoolRegistry.HOLY.get()) return AttributeRegistry.HOLY_SPELL_POWER.get();
        if (school == SchoolRegistry.ENDER.get()) return AttributeRegistry.ENDER_SPELL_POWER.get();
        if (school == SchoolRegistry.BLOOD.get()) return AttributeRegistry.BLOOD_SPELL_POWER.get();
        if (school == SchoolRegistry.EVOCATION.get()) return AttributeRegistry.EVOCATION_SPELL_POWER.get();
        if (school == SchoolRegistry.NATURE.get()) return AttributeRegistry.NATURE_SPELL_POWER.get();
        if (school == SchoolRegistry.ELDRITCH.get()) return AttributeRegistry.ELDRITCH_SPELL_POWER.get();
        return null;
    }

    private Attribute getMagicResistForSchool(SchoolType school) {
        if (school == SchoolRegistry.FIRE.get()) return AttributeRegistry.FIRE_MAGIC_RESIST.get();
        if (school == SchoolRegistry.ICE.get()) return AttributeRegistry.ICE_MAGIC_RESIST.get();
        if (school == SchoolRegistry.LIGHTNING.get()) return AttributeRegistry.LIGHTNING_MAGIC_RESIST.get();
        if (school == SchoolRegistry.HOLY.get()) return AttributeRegistry.HOLY_MAGIC_RESIST.get();
        if (school == SchoolRegistry.ENDER.get()) return AttributeRegistry.ENDER_MAGIC_RESIST.get();
        if (school == SchoolRegistry.BLOOD.get()) return AttributeRegistry.BLOOD_MAGIC_RESIST.get();
        if (school == SchoolRegistry.EVOCATION.get()) return AttributeRegistry.EVOCATION_MAGIC_RESIST.get();
        if (school == SchoolRegistry.NATURE.get()) return AttributeRegistry.NATURE_MAGIC_RESIST.get();
        if (school == SchoolRegistry.ELDRITCH.get()) return AttributeRegistry.ELDRITCH_MAGIC_RESIST.get();
        return null;
    }

    @Override
    public void verifyTagAfterLoad(@NotNull CompoundTag nbt) {
        ToolStack.verifyTag(this, nbt, this.getToolDefinition());
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Player playerIn) {
        ToolStack.ensureInitialized(stack, this.getToolDefinition());
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return ModifierUtil.checkVolatileFlag(stack, SHINY);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return RarityModule.getRarity(stack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return IndestructibleItemEntity.hasCustomEntity(stack);
    }

    @Override
    public Entity createEntity(Level world, Entity original, ItemStack stack) {
        return IndestructibleItemEntity.createFrom(world, original, stack);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TooltipUtil.getDisplayName(stack, this.getToolDefinition());
    }

    @Override
    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return TooltipUtil.getModifierHideFlags(this.getToolDefinition());
    }

    @Override
    public @NotNull ItemStack getRenderTool() {
        if (this.toolForRendering == null) {
            this.toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }

        return this.toolForRendering;
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack != null) {
            if (!ISpellContainer.isSpellContainer(itemStack)) {
                ISpellContainer.set(itemStack, ISpellContainer.create(this.getMaxSpellSlots(), true, true));
            }

        }
    }

    @Override
    public @NotNull ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }
}
