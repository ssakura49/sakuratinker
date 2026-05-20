package com.ssakura49.sakuratinker.data.generator.providiers.tinker;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSCompat;
import com.ssakura49.sakuratinker.data.generator.base.BaseRecipeProvider;
import com.ssakura49.sakuratinker.register.STItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.ssakura49.sakuratinker.utils.SafeClassUtil.Modid.ISS;

public class STToolRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper {
    public STToolRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    public @NotNull String getName() {
        return "SakuraTinker Tool Recipe Provider";
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        addPartRecipes(consumer);
        addToolBuildingRecipes(consumer);
    }

    private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        toolBuilding(consumer, STItems.tic_fox_mask,folder);
    }

    private void addPartRecipes(Consumer<FinishedRecipe> consumer) {
        String partFolder = "tools/parts/";
        String castFolder = "smeltery/casts/";


        Consumer<FinishedRecipe> conditional;
        //ISS
        conditional = withCondition(consumer,modLoaded(ISS));
        registerPart(conditional, ISSCompat.book_mark, ISSCompat.bookMarkCast, 3, partFolder, castFolder);
        registerPart(conditional, ISSCompat.envelope, ISSCompat.envelopeCast, 8, partFolder,castFolder);
        registerPart(conditional,ISSCompat.manu_script,ISSCompat.manuScriptCast, 6,partFolder,castFolder);
        registerPart(conditional,ISSCompat.gutter,ISSCompat.gutterCast,4,partFolder,castFolder);
    }

    private void addRecycleRecipes(Consumer<FinishedRecipe> consumer) {

    }

    private void registerPart(Consumer<FinishedRecipe> consumer, Supplier<? extends IMaterialItem> part, CastItemObject cast, int cost, String partFolder, String castFolder) {
        this.registerPart(consumer,(IMaterialItem) part.get(),cast,cost,partFolder,castFolder);
    }

    private void registerPart(Consumer<FinishedRecipe> consumer, IMaterialItem part, CastItemObject cast, int cost, String partFolder, String castFolder) {
        ResourceLocation partId = id(part);
        String name = partId.getPath();
        TagKey<Item> singleTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tconstruct", "casts/single_use/" + name));
        TagKey<Item> multiTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tconstruct", "casts/multi_use/" + name));
        PartRecipeBuilder.partRecipe(part)
                .setPattern(partId)
                .setPatternItem(CompoundIngredient.of(
                        Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS),
                        Ingredient.of(cast.get())
                ))
                .setCost(cost)
                .save(consumer, location(partFolder + "builder/" + name));
        String castingFolder = partFolder + "casting/";
        MaterialCastingRecipeBuilder.tableRecipe(part)
                .setItemCost(cost)
                .setCast(multiTag, false)
                .save(consumer, location(castingFolder + name + "_gold_cast"));
        MaterialCastingRecipeBuilder.tableRecipe(part)
                .setItemCost(cost)
                .setCast(singleTag, true)
                .save(consumer, location(castingFolder + name + "_sand_cast"));
        CompositeCastingRecipeBuilder.table(part, cost)
                .save(consumer, location(castingFolder + name + "_composite"));
        this.castCreation(consumer, MaterialIngredient.of(part), cast, castFolder, name);
    }

}
