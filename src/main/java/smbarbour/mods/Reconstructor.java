package smbarbour.mods;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "Reconstructor", name="Reconstructor", version="1.3", acceptedMinecraftVersions="[1.6,1.7],")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Reconstructor {
	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	public boolean useBCRecipe;
	public boolean useTERecipe;
	public boolean doPipeInteract = false;
	@Instance("Reconstructor")
	public static Reconstructor instance;

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		NetworkRegistry.instance().registerGuiHandler(instance, new ReconGuiHandler());
		GameRegistry.registerTileEntity(TileRecon.class, "Reconstructor");
		loadRecipes();
	}
	
	@EventHandler
	public void initialize(FMLPreInitializationEvent evt) {
		config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		int reconId = config.getBlock("reconstructor.id",3000).getInt(3000);
		energyPerPoint = config.get("General", "MJ_per_damage_point", 5).getInt(5);
		useBCRecipe = config.get("General", "BC_Recipe", true).getBoolean(true);
		useTERecipe = config.get("General", "TE_Recipe", true).getBoolean(true);
		if (config.hasChanged()) {
			config.save();
		}
		
		reconBlock = new BlockRecon(reconId);
		GameRegistry.registerBlock(reconBlock, ItemBlockReconstructor.class, reconBlock.getUnlocalizedName().replace("tile.", ""));
		LanguageRegistry.addName(new ItemStack(reconBlock), "Reconstructor");
		
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		if (Loader.isModLoaded("BuildCraft|Transport")) {
			doPipeInteract = true;
		}
	}

	private void loadRecipes() {
		ShapedOreRecipe gearRecipeBC = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gpg", // )
				'i', Item.ingotIron,
				'a', Block.anvil,
				'g', "gearDiamond",
				'p', Block.chest
		);
		ShapedOreRecipe gearRecipeTE = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gpg", // )
				'i', Item.ingotIron,
				'a', Block.anvil,
				'g', "gearInvar",
				'p', Block.chest
		);
		if (useBCRecipe) {
			GameRegistry.addRecipe(gearRecipeBC);
		}
		if (useTERecipe) {
			GameRegistry.addRecipe(gearRecipeTE);
		}
	}

}
