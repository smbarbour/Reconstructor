package smbarbour.mods;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "Reconstructor", name="Reconstructor", version="1.5", dependencies = "required-after:BuildCraft|Core")
public class Reconstructor {
	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	public boolean useBCRecipe;
	public boolean useTERecipe;
	public boolean doPipeInteract = false;
	public boolean restrictRepairs;
	@Instance("Reconstructor")
	public static Reconstructor instance;

    @EventHandler
    public void initialize(FMLPreInitializationEvent evt) {
        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();
        energyPerPoint = config.get("General", "MJ_per_damage_point", 5).getInt(5);
        useBCRecipe = config.get("General", "BC_Recipe", true).getBoolean(true);
        useTERecipe = config.get("General", "TE_Recipe", true).getBoolean(true);
        restrictRepairs = config.get("General", "Restricted", false, "If true, will only repair things that extend the tool, armor, sword and bow classes.").getBoolean(false);
        if (config.hasChanged()) {
            config.save();
        }

        reconBlock = new BlockRecon();
        GameRegistry.registerBlock(reconBlock, ItemBlockReconstructor.class, reconBlock.getUnlocalizedName().replace("tile.", ""));

        MinecraftForge.EVENT_BUS.register(this);
    }

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ReconGuiHandler());
		GameRegistry.registerTileEntity(TileRecon.class, "Reconstructor");
		loadRecipes();
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
				'i', Items.iron_ingot,
				'a', Blocks.anvil,
				'g', "gearDiamond",
				'p', Blocks.chest
		);
		ShapedOreRecipe gearRecipeTE = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gpg", // )
				'i', Items.iron_ingot,
				'a', Blocks.anvil,
				'g', "gearInvar",
				'p', Blocks.chest
		);
		if (useBCRecipe) {
			GameRegistry.addRecipe(gearRecipeBC);
		}
		if (useTERecipe) {
			GameRegistry.addRecipe(gearRecipeTE);
		}
	}

}
