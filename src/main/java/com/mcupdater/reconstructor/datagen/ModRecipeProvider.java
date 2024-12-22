package com.mcupdater.reconstructor.datagen;

import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Registration.RECONSTRUCTOR_BLOCK.get())
                .define('C', Ingredient.of(Items.COPPER_INGOT))
                .define('F',Ingredient.of(Items.IRON_INGOT))
                .define('#', Ingredient.of(Blocks.GRINDSTONE))
                .define('R', Ingredient.of(Items.REDSTONE))
                .pattern("CFC")
                .pattern("F#F")
                .pattern("CRC")
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(output);
    }
}
