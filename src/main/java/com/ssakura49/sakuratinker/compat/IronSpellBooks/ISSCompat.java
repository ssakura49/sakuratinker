package com.ssakura49.sakuratinker.compat.IronSpellBooks;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.item.TinkerSpellBook;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.BookMarkMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.EnvelopeMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ISSStatlessMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.tool.stats.ManuScriptMaterialStats;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.ArcaneConstructModifier;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.ElementalMasteryModifier;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.FountainMagicModifier;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.MagicianModifier;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers.attribute.*;
import com.ssakura49.sakuratinker.library.tinkering.tools.item.ModifiableSpellBookItem;
import net.minecraft.world.item.Item;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class ISSCompat {
    public static ModifierDeferredRegister ISS_MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);
    public static final ItemDeferredRegisterExtension TINKER_ISS_ITEMS = new ItemDeferredRegisterExtension(SakuraTinker.MODID);

    public static final Item.Properties PartItem = new Item.Properties().stacksTo(64);
    public static final Item.Properties CastItem = new Item.Properties().stacksTo(64);
    public static final Item.Properties ToolItem = new Item.Properties().stacksTo(1);

    public static final ItemObject<ToolPartItem> mana_script = TINKER_ISS_ITEMS.register("mana_script", () -> new ToolPartItem(PartItem, ManuScriptMaterialStats.ID));
    public static final ItemObject<ToolPartItem> envelope = TINKER_ISS_ITEMS.register("envelope", () -> new ToolPartItem(PartItem, EnvelopeMaterialStats.ID));
    public static final ItemObject<ToolPartItem> book_mark = TINKER_ISS_ITEMS.register("book_mark", () -> new ToolPartItem(PartItem, BookMarkMaterialStats.ID));
    public static final ItemObject<ToolPartItem> gutter = TINKER_ISS_ITEMS.register("gutter", () -> new ToolPartItem(PartItem, ISSStatlessMaterialStats.GUTTER.getIdentifier()));

    public static final ItemObject<ModifiableSpellBookItem> tinker_spell_book = TINKER_ISS_ITEMS.register("tinker_spell_book", () -> new TinkerSpellBook(1,ToolItem));

    public static final CastItemObject bookCoverCast = TINKER_ISS_ITEMS.registerCast("book_cover", CastItem);
    public static final CastItemObject spellClothCast = TINKER_ISS_ITEMS.registerCast("spell_cloth", CastItem);


    public static StaticModifier<MagicianModifier> Magician;
    public static StaticModifier<FountainMagicModifier> FountainMagic;
    public static StaticModifier<ElementalMasteryModifier> ElementalMastery;
    public static StaticModifier<ArcaneConstructModifier> ArcaneTinkering;


    public static StaticModifier<ENDER_ATTR> ENDER_ATTR;
    public static StaticModifier<FIRE_ATTR> FIRE_ATTR;
    public static StaticModifier<LIGHTNING_ATTR> LIGHTNING_ATTR;
    public static StaticModifier<HOLY_ATTR> HOLY_ATTR;
    public static StaticModifier<BLOOD_ATTR> BLOOD_ATTR;
    public static StaticModifier<ICE_ATTR> ICE_ATTR;
    public static StaticModifier<EVOCATION_ATTR> EVOCATION_ATTR;
    public static StaticModifier<NATURE_ATTR> NATURE_ATTR;
    public static StaticModifier<MANA_ATTR> MANA_ATTR;
    public static StaticModifier<SPELL_COOLDOWN_ATTR> SPELL_COOLDOWN_ATTR;
    public static StaticModifier<SPELL_PROTECTION_ATTR> SPELL_PROTECTION_ATTR;
    static {
        Magician = ISS_MODIFIERS.register("magician", MagicianModifier::new);
        FountainMagic = ISS_MODIFIERS.register("fountain_magic", FountainMagicModifier::new);
        ElementalMastery = ISS_MODIFIERS.register("elemental_mastery", ElementalMasteryModifier::new);
        ArcaneTinkering = ISS_MODIFIERS.register("arcane_tinkering", ArcaneConstructModifier::new);

        ENDER_ATTR = ISS_MODIFIERS.register("ender_attr", ENDER_ATTR::new);
        FIRE_ATTR = ISS_MODIFIERS.register("fire_attr", FIRE_ATTR::new);
        LIGHTNING_ATTR = ISS_MODIFIERS.register("lightning_attr", LIGHTNING_ATTR::new);
        HOLY_ATTR = ISS_MODIFIERS.register("holy_attr", HOLY_ATTR::new);
        BLOOD_ATTR = ISS_MODIFIERS.register("blood_attr", BLOOD_ATTR::new);
        ICE_ATTR = ISS_MODIFIERS.register("ice_attr", ICE_ATTR::new);
        EVOCATION_ATTR = ISS_MODIFIERS.register("evocation_attr", EVOCATION_ATTR::new);
        NATURE_ATTR = ISS_MODIFIERS.register("nature_attr", NATURE_ATTR::new);
        MANA_ATTR = ISS_MODIFIERS.register("mana_attr", MANA_ATTR::new);
        SPELL_COOLDOWN_ATTR = ISS_MODIFIERS.register("spell_cooldown_attr", SPELL_COOLDOWN_ATTR::new);
        SPELL_PROTECTION_ATTR = ISS_MODIFIERS.register("spell_protection_attr", SPELL_PROTECTION_ATTR::new);
    }
}
