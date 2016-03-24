package org.mcupdater.reconstructor;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod(modid = "Reconstructor", useMetadata = true)
public class Reconstructor {
	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	public boolean restrictRepairs;
	@Instance("Reconstructor")
	public static Reconstructor instance;
	private String recipeItem;
	public static Set<String> blacklist;
	public static Property blProperty;

	@EventHandler
    public void initialize(FMLPreInitializationEvent evt) {
        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();
        energyPerPoint = config.get("General", "RF_per_damage_point", 50).getInt(50);
		recipeItem = config.get("General", "Recipe_Item", "gearInvar", "Ore dictionary string of item to use in lower corners").getString();
        restrictRepairs = config.get("General", "Restricted", false, "If true, will only repair things that extend the tool, armor, sword and bow classes.").getBoolean(false);
		blProperty = config.get("General", "Blacklist", new String[0], "Item classes that appear in this list will not be repaired by the Reconstructor.");
		blacklist = new HashSet<String>(Arrays.asList(blProperty.getStringList()));
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
	public void serverStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new AddBlacklistCommand());
	}
	
	private void loadRecipes() {
        ItemStack keyItem = GameRegistry.findItemStack("ThermalExpansion","powerCoilGold",1);
        if (keyItem == null) {
            keyItem = new ItemStack(Items.redstone);
        }
		ShapedOreRecipe gearRecipe = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gcg", // )
				'i', Items.iron_ingot,
				'a', Blocks.anvil,
				'g', recipeItem,
				'c', keyItem
		);
		GameRegistry.addRecipe(gearRecipe);
	}

}
