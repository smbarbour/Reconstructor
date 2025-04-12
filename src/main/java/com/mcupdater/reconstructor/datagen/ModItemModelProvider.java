package com.mcupdater.reconstructor.datagen;

import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemModelProvider extends ItemModelProvider {
	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, Reconstructor.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
				block((BlockItem) Registration.RECONSTRUCTOR_BLOCKITEM.get());
				withExistingParent(
						BuiltInRegistries.ITEM.getKey(Registration.PORTABLE_RECONSTRUCTOR.get()).getPath(),
						ResourceLocation.withDefaultNamespace("item/generated")
						)
						.texture("layer0", ResourceLocation.fromNamespaceAndPath(Reconstructor.MODID,"item/handheld_blank"))
						.texture("layer1", ResourceLocation.fromNamespaceAndPath(Reconstructor.MODID, "block/hammer"))
				;
	}

	protected ItemModelBuilder block(BlockItem blockItem) {
		return withExistingParent(BuiltInRegistries.ITEM.getKey(blockItem).getPath(),modid + ":block/" + BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).getPath());
	}
}
