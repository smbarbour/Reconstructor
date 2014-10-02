package org.mcupdater.reconstructor;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thermalexpansion.item.TEItems;

@Mod(modid = "Reconstructor", name="Reconstructor", version="2.0", dependencies = "required-after:CoFHCore;required-after:ThermalExpansion")
public class Reconstructor {
	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	public boolean doPipeInteract = false;
	public boolean restrictRepairs;
	@Instance("Reconstructor")
	public static Reconstructor instance;

    @EventHandler
    public void initialize(FMLPreInitializationEvent evt) {
        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();
        energyPerPoint = config.get("General", "RF_per_damage_point", 50).getInt(50);
        /*
        useBCRecipe = config.get("General", "BC_Recipe", true).getBoolean(true);
        useTERecipe = config.get("General", "TE_Recipe", true).getBoolean(true);
        */
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
	
	private void loadRecipes() {
		ShapedOreRecipe gearRecipeTE = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gcg", // )
				'i', Items.iron_ingot,
				'a', Blocks.anvil,
				'g', "gearInvar",
				'c', TEItems.powerCoilGold
		);
		GameRegistry.addRecipe(gearRecipeTE);
	}

}
