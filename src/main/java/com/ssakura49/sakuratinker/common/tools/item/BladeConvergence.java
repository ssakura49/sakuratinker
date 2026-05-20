package com.ssakura49.sakuratinker.common.tools.item;

import com.google.common.base.Suppliers;
import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.common.entity.CelestialBladeEntity;
import com.ssakura49.sakuratinker.common.entity.item.CelestialBladePart;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.register.STItems;
import com.ssakura49.sakuratinker.register.STSounds;
import com.ssakura49.sakuratinker.utils.tinker.TooltipUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class BladeConvergence extends ModifiableItem {
    private static final String TAG_ZENITH_PARTS = "zenith_parts";
    private static final Supplier<List<CelestialBladePart>> DEFAULT_PARTS = Suppliers.memoize(BladeConvergence::createZenithParts);
    private static final Supplier<List<CelestialBladePart>> DEFAULT_SLAUGHTER_PARTS = Suppliers.memoize(BladeConvergence::createDefaultParts);
    private static final Supplier<Boolean> ENABLE_ALL_RANDOM = STConfig.Client.ENABLE_ZENITH_ALL_RANDOM_MATERIALS;
    private static final Supplier<Boolean> CAN_TRIGGER_MODIFIER = STConfig.Common.BLADE_CONVERGENCE_CAN_TRIGGER_MODIFIER;

    public BladeConvergence(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ToolStack tool = ToolStack.from(stack);

        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            float damage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
            float speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
            float range = tool.getStats().get(STToolStats.RANGE);
            int cooldown = (int)(20 / speed);
            player.getCooldowns().addCooldown(this, cooldown);


            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    STSounds.CELESTIAL_BLADE.get(), SoundSource.PLAYERS,
                    1.0F, 0.9F + level.random.nextFloat() * 0.2F);

            CelestialBladeEntity entity = new CelestialBladeEntity(
                    level,
                    player,
                    tool,
                    hand,
                    damage
            );

            List<CelestialBladePart> parts = getZenithParts(stack);
            CelestialBladePart selectedPart = parts.get(player.getRandom().nextInt(parts.size()));
            entity.setZenithPart(selectedPart);

            ItemStack displayStack = createRandomizedTinkerStack(selectedPart.item().value());

            if (CAN_TRIGGER_MODIFIER.get()) {
                entity.setTool(displayStack);
            } else {
                entity.setTool(stack);
            }
            entity.setItemStack(displayStack);
            entity.setRange(range);
            level.addFreshEntity(entity);

            ToolDamageUtil.damageAnimated(tool, 1, player);

            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @Override
    public @NotNull List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag flag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
        builder.addDurability();
        builder.add(ToolStats.ATTACK_DAMAGE);
        builder.add(ToolStats.ATTACK_SPEED);
        TooltipUtil.addToolStatTooltip(builder, tool, STToolStats.COOLDOWN);
        TooltipUtil.addToolStatTooltip(builder, tool, STToolStats.RANGE);
        builder.addAllFreeSlots();

        for(ModifierEntry entry : tool.getModifierList()) {
            ((TooltipModifierHook)entry.getHook(ModifierHooks.TOOLTIP)).addTooltip(tool, entry, player, tooltips, key, flag);
        }

        return tooltips;
    }

    @SuppressWarnings("deprecation")
    public static List<CelestialBladePart> createDefaultParts() {
        return List.of(
                new CelestialBladePart(STItems.slaughter.get().builtInRegistryHolder(), 0x594319, 0.125, Mth.HALF_PI * 0.5f, 1.75, 1)
        );
    }
    @SuppressWarnings("deprecation")
    public static List<CelestialBladePart> createZenithParts() {
        return List.of(
                new CelestialBladePart(TinkerTools.pickaxe.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.75, 1.5),
                new CelestialBladePart(TinkerTools.sledgeHammer.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2, 1.6),
                new CelestialBladePart(TinkerTools.veinHammer.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2, 1.6),
                new CelestialBladePart(TinkerTools.mattock.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.75, 1.5),
                new CelestialBladePart(TinkerTools.pickadze.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.75, 1.5),
                new CelestialBladePart(TinkerTools.excavator.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2, 1.6),
                new CelestialBladePart(TinkerTools.handAxe.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.5, 1.5),
                new CelestialBladePart(TinkerTools.broadAxe.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.5, 1.7),
                new CelestialBladePart(TinkerTools.kama.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.5, 1.5),
                new CelestialBladePart(TinkerTools.scythe.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.5, 1.7),
                new CelestialBladePart(TinkerTools.dagger.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1, 1.5),
                new CelestialBladePart(TinkerTools.sword.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2, 1.6),
                new CelestialBladePart(TinkerTools.cleaver.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 3.5, 1.8),
                new CelestialBladePart(TinkerTools.meltingPan.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.3, 1.5),
                new CelestialBladePart(TinkerTools.warPick.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 3.5, 1.8),
                new CelestialBladePart(TinkerTools.battlesign.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 3, 1.5),
                new CelestialBladePart(TinkerTools.swasher.get().builtInRegistryHolder(),0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.7, 1.5),
                new CelestialBladePart(STItems.great_sword.get().builtInRegistryHolder(), 0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 5.5, 2.1),
                new CelestialBladePart(STItems.swift_sword.get().builtInRegistryHolder(), 0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.5, 1.5),
                new CelestialBladePart(STItems.vampire_knife.get().builtInRegistryHolder(), 0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 1.4, 1.4),
                new CelestialBladePart(STItems.blade_convergence.get().builtInRegistryHolder(), 0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.6, 1.7),
                new CelestialBladePart(STItems.scythe.get().builtInRegistryHolder(), 0xb2ffb4, 0.125, Mth.HALF_PI * 0.5f, 2.2, 1.5)

        );
    }
    public static List<CelestialBladePart> getZenithParts(ItemStack stack) {
        List<CelestialBladePart> parts = new ArrayList<>();
        CelestialBladePart.LIST_CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get(TAG_ZENITH_PARTS)).result().ifPresent(parts::addAll);
        if (parts.isEmpty()) {
            parts.addAll(stack.is(STItems.slaughter.get()) ? DEFAULT_SLAUGHTER_PARTS.get() : DEFAULT_PARTS.get());
        }
        return parts;
    }

    public static ItemStack createRandomizedTinkerStack(Item item) {
        if (item instanceof ModifiableItem modifiable) {
            ToolDefinition definition = modifiable.getToolDefinition();
            //利用MISSING_MATERIALS钩子自动填充随机材质
            //匠魂自带的这个方法会根据工具定义要求的StatType自动寻找首个有效材质
            MaterialNBT randomMaterials = ENABLE_ALL_RANDOM.get() ? fillTrulyRandom(definition) : definition.getHook(ToolHooks.MISSING_MATERIALS).fillMaterials(definition, RandomSource.create());

            return ToolStack.createTool(item, definition, randomMaterials).createStack();
        }
        return new ItemStack(item);
    }


    public static MaterialNBT fillTrulyRandom(ToolDefinition definition) {
        MaterialNBT.Builder builder = MaterialNBT.builder();
        for (MaterialStatsId statType : ToolMaterialHook.stats(definition)) {
            List<IMaterial> allPossible = MaterialRegistry.getInstance().getAllMaterials().stream()
                    .filter(mat -> MaterialRegistry.getInstance().getMaterialStats(mat.getIdentifier(), statType).isPresent())
                    .toList();

            if (!allPossible.isEmpty()) {
                builder.add(allPossible.get(new Random().nextInt(allPossible.size())));
            } else {
                builder.add(MaterialRegistry.firstWithStatType(statType));
            }
        }
        return builder.build();
    }
}
