package com.ssakura49.sakuratinker.utils.tinker;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.tinkercuriolib.utils.TCToolUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;
import java.util.function.Predicate;

public class ToolUtil {
    public ToolUtil() {
    }

    public static boolean checkTool(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IModifiable modifiable) {
                return modifiable.getToolDefinition() != ToolDefinition.EMPTY;
            }
        }
        return false;
    }

    // 用 ThreadLocal 保存当前实体
    private static final ThreadLocal<LivingEntity> CURRENT_ENTITY = new ThreadLocal<>();

    /** 设置当前实体（进入事件时调用） */
    public static void setCurrentEntity(@Nullable LivingEntity entity) {
        if (entity == null) {
            CURRENT_ENTITY.remove();
        } else {
            CURRENT_ENTITY.set(entity);
        }
    }

    /** 获取当前实体（Modifier 内部调用） */
    @Nullable
    public static LivingEntity getCurrentEntity() {
        return CURRENT_ENTITY.get();
    }

    public static List<ModifierEntry> getAllModifiers(LivingEntity living) {
        List<ModifierEntry> result = new ArrayList<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = living.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(stack);
                if (!tool.isBroken()) {
                    result.addAll(tool.getModifierList());
                }
            }
        }

        return result;
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Modifier modifier) {
        return getToolStack(tool, entity, stack -> ToolStack.from(stack).getModifierLevel(modifier) > 0);
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Predicate<ItemStack> predicate) {
        if (tool instanceof ToolStack) {
            return ((ToolStack) tool).createStack();
        }
        ItemStack mainHandStack = entity.getMainHandItem();
        if (mainHandStack.getItem() instanceof IModifiable && predicate.test(mainHandStack)) {
            return mainHandStack;
        }
        ItemStack offHandStack = entity.getOffhandItem();
        if (offHandStack.getItem() instanceof IModifiable && predicate.test(offHandStack)) {
            return offHandStack;
        }

        if (ModList.get().isLoaded("curios")) {
            ItemStack curioStack = TCToolUtil.getToolStackInCurios(entity, predicate);
            if (curioStack != null) {
                return curioStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static String getEquipmentSlotName(LivingEntity entity, ItemStack stack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (ItemStack.isSameItem(entity.getItemBySlot(slot), stack)) {
                return slot.getName();
            }
        }

        if (ModList.get().isLoaded("curios")) {
            return TCToolUtil.getEquipmentSlotNameInCurios(entity, stack);
        }

        return null;
    }

    public static boolean isCosmicRendererTools(List<MaterialVariantId> list) {
        MaterialVariantId infinityMaterial = new MaterialId(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "infinity"));
        return list.contains(infinityMaterial);
    }

    @Nullable
    public static ToolStack getTool(ItemStack stack) {
        if (checkTool(stack)) {
            return ToolStack.from(stack);
        }
        return null;
    }

    @Nullable
    public static ToolStack getToolInHand(LivingEntity entity) {
        ItemStack offItem = entity.getOffhandItem();
        ItemStack mainItem = entity.getMainHandItem();
        ItemStack stack = mainItem.isEmpty() ? (offItem.isEmpty() ? null : offItem) : mainItem;
        return stack != null ? getTool(stack) : null;
    }

    @Nullable
    public static ToolStack getHeldTool(LivingEntity entity, EquipmentSlot slot) {
        return Modifier.getHeldTool(entity, slot);
    }

    public static boolean hasModifierInHeldTool(LivingEntity entity, Modifier modifier) {
        ToolStack tool = getToolInHand(entity);
        if (isNotBrokenOrNull(tool)) {
            return tool.getModifierLevel(modifier) != 0;
        } else {
            return false;
        }
    }

    public static int getModifierInHeldTool(LivingEntity entity, Modifier modifier) {
        ToolStack tool = getToolInHand(entity);
        return isNotBrokenOrNull(tool) ? tool.getModifierLevel(modifier) : 0;
    }

    @Nullable
    public static ToolStack getNotBrokenAndCooldownArmor(LivingEntity entity) {
        for(ItemStack stack : entity.getArmorSlots()) {
            ToolStack tool = getTool(stack);
            if (isNotBrokenOrNull(tool) && ItemUtil.noCooldown(entity, tool)) {
                return tool;
            }
        }

        return null;
    }

    @Nullable
    public static ToolStack getNotBrokenArmor(LivingEntity entity) {
        for(ItemStack stack : entity.getArmorSlots()) {
            ToolStack tool = getTool(stack);
            if (isNotBrokenOrNull(tool)) {
                return tool;
            }
        }
        return null;
    }

    @Nullable
    public static ToolStack getNotBrokenToolInHand(LivingEntity entity) {
        if (getHeldTool(entity, EquipmentSlot.OFFHAND) != null) {
            return getHeldTool(entity, EquipmentSlot.OFFHAND);
        } else {
            return getHeldTool(entity, EquipmentSlot.MAINHAND) != null ? getHeldTool(entity, EquipmentSlot.MAINHAND) : null;
        }
    }

    public static List<IToolStackView> getAllEquippedToolStacks(Player player) {
        List<IToolStackView> tools = new ArrayList<>();
        for (ItemStack stack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
            ToolStack tool = getTool(stack);
            if (isNotBrokenOrNull(tool)) {
                tools.add(tool);
            }
        }
        for (ItemStack stack : player.getArmorSlots()) {
            ToolStack tool = getTool(stack);
            if (isNotBrokenOrNull(tool)) {
                tools.add(tool);
            }
        }
        for (ItemStack stack : TCToolUtil.getStacks(player)) {
            ToolStack tool = getTool(stack);
            if (isNotBrokenOrNull(tool)) {
                tools.add(tool);
            }
        }

        return tools;
    }

    public static int getHeadModifierLevel(LivingEntity entity, Modifier modifier) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (checkTool(stack)) {
            ToolStack toolStack = ToolStack.from(stack);
            return !toolStack.isBroken() ? toolStack.getModifierLevel(modifier) : 0;
        }
        return 0;
    }

    public static int getChestModifierLevel(LivingEntity entity, Modifier modifier) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (checkTool(stack)) {
            ToolStack toolStack = ToolStack.from(stack);
            return !toolStack.isBroken() ? toolStack.getModifierLevel(modifier) : 0;
        }
        return 0;
    }

    public static int getLegsModifierLevel(LivingEntity entity, Modifier modifier) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (checkTool(stack)) {
            ToolStack toolStack = ToolStack.from(stack);
            return !toolStack.isBroken() ? toolStack.getModifierLevel(modifier) : 0;
        }
        return 0;
    }

    public static int getFeetModifierLevel(LivingEntity entity, Modifier modifier) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.FEET);
        if (checkTool(stack)) {
            ToolStack toolStack = ToolStack.from(stack);
            return !toolStack.isBroken() ? toolStack.getModifierLevel(modifier) : 0;
        }
        return 0;
    }

    public static boolean hasModifierInAllArmor(LivingEntity entity, Modifier modifier) {
        return getHeadModifierLevel(entity, modifier) > 0 && getChestModifierLevel(entity, modifier) > 0 && getLegsModifierLevel(entity, modifier) > 0 && getFeetModifierLevel(entity, modifier) > 0;
    }

    public static int getModifierArmorAllLevel(LivingEntity entity, Modifier modifier) {
        return getHeadModifierLevel(entity, modifier) + getChestModifierLevel(entity, modifier) + getLegsModifierLevel(entity, modifier) + getFeetModifierLevel(entity, modifier);
    }

    public static boolean isNotBrokenOrNull(IToolStackView tool) {
        return tool != null && !tool.isBroken();
    }

    public static boolean hasModifierIn(IToolStackView tool, Modifier modifier) {
        return tool.getModifierLevel(modifier) > 0;
    }

    public static boolean hasModifierIn(IToolStackView tool, ResourceLocation id) {
        return tool.getModifierLevel(new ModifierId(id)) > 0;
    }

    public static boolean hasMetaIn(ToolStack tool, String id) {
        Iterator<MaterialVariant> var2 = tool.getMaterials().getList().iterator();
        if (var2.hasNext()) {
            MaterialVariant material = (MaterialVariant)var2.next();
            return material.get().getIdentifier().toString().equals(id);
        } else {
            return false;
        }
    }

    public static boolean hasMetasIn(ToolStack tool, String... ids) {
        Set<String> idSet = new HashSet<>(Arrays.asList(ids));
        for (MaterialVariant material : tool.getMaterials().getList()) {
            String materialId = material.get().getIdentifier().toString();
            if (idSet.contains(materialId)) {
                return true;
            }
        }
        return false;
    }

    public static float getStatValue(ModifierStatsBuilder builder, INumericToolStat<?> stat, boolean multiplier) {
        float value = ((Number)builder.getStat(stat)).floatValue();
        return multiplier ? value * builder.getMultiplier(stat) : value;
    }

    public static List<ModifierEntry> getPartTraits(ItemStack item) {
        Item var2 = item.getItem();
        if (var2 instanceof IToolPart part) {
            return MaterialRegistry.getInstance().getTraits(part.getMaterial(item).getId(), part.getStatType());
        } else {
            return List.of();
        }
    }

    public static boolean canShoot(IToolStackView tool, Player player) {
        return !player.level().isClientSide && tool.getCurrentDurability() > 0 && player.getAttackStrengthScale(0.5F) > 0.9F;
    }

//    public static class Curios {
//        public Curios() {
//        }
//        public static List<ItemStack> getStacks(LivingEntity entity) {
//            List<ItemStack> list = new ArrayList<>();
//
//            CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
//                handler.getCurios().forEach((id, curios) -> {
//                    for (int i = 0; i < curios.getSlots(); ++i) {
//                        ItemStack stack = curios.getStacks().getStackInSlot(i);
//                        if (!stack.isEmpty() && stack.is(Items.MODIFIABLE)) {
//                            list.add(stack);
//                        }
//                    }
//                });
//            });
//
//            return list;
//        }
//
//        public static List<ItemStack> getAllToolStackInCurios(LivingEntity entity, Predicate<ItemStack> predicate) {
//            List<ItemStack> result = new ArrayList<>();
//            LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
//            if (handlerOptional.isPresent()) {
//                ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
//                IItemHandlerModifiable curiosInv = handler.getEquippedCurios();
//                for (int i = 0; i < handler.getSlots(); i++) {
//                    ItemStack curio = curiosInv.getStackInSlot(i);
//                    if (predicate.test(curio)) {
//                        result.add(curio);
//                    }
//                }
//            }
//            return result;
//        }
//
//        public static ItemStack getToolStackInCurios(LivingEntity entity, Predicate<ItemStack> predicate) {
//            LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
//            if (handlerOptional.isPresent()) {
//                ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
//                IItemHandlerModifiable curiosInv = handler.getEquippedCurios();
//                for (int i = 0; i < handler.getSlots(); i++) {
//                    ItemStack curio = curiosInv.getStackInSlot(i);
//                    if (predicate.test(curio)) {
//                        return curio;
//                    }
//                }
//            }
//            return null;
//        }
//
//        public static String getEquipmentSlotNameInCurios(LivingEntity entity, ItemStack stack) {
//            LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
//            if (handlerOptional.isPresent()) {
//                ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
//                Optional<SlotResult> slotResultOptional = handler.findFirstCurio(curio -> ItemStack.isSameItem(stack, curio)
//                );
//                if (slotResultOptional.isPresent()) {
//                    SlotResult slotResult = slotResultOptional.orElseThrow(IllegalStateException::new);
//                    return slotResult.slotContext().identifier();
//                }
//            }
//            return null;
//        }
//
//    }
}
