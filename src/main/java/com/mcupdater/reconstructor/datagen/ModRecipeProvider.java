package com.mcupdater.reconstructor.datagen;

import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(Registration.RECONSTRUCTOR_BLOCK.get()).define('C', Ingredient.of(Items.COPPER_INGOT)).define('F',Ingredient.of(Items.IRON_INGOT)).define('#', Ingredient.of(Blocks.GRINDSTONE)).define('R', Ingredient.of(Items.REDSTONE)).pattern("CFC").pattern("F#F").pattern("CRC").unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(finishedRecipeConsumer);
    }
}
