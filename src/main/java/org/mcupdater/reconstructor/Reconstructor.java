package org.mcupdater.reconstructor;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.mcupdater.reconstructor.proxy.CommonProxy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod(modid = "Reconstructor", useMetadata = true)
public class Reconstructor {

	@SidedProxy(clientSide = "org.mcupdater.reconstructor.proxy.ClientProxy", serverSide = "org.mcupdater.reconstructor.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	public static ModMetadata metadata;
	public boolean restrictRepairs;
	@Mod.Instance("Reconstructor")
	public static Reconstructor instance;
	private String recipeItem;
	public static Set<String> blacklist;
	public static Property blProperty;

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
		metadata = evt.getModMetadata();
        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();
        energyPerPoint = config.get("General", "RF_per_damage_point", 50).getInt(50);
		recipeItem = config.get("General", "Recipe_Item", "minecraft:book", "Item ID or Ore dictionary string of item to use in lower corners").getString();
        restrictRepairs = config.get("General", "Restricted", false, "If true, will only repair things that extend the tool, armor, sword and bow classes.").getBoolean(false);
		blProperty = config.get("General", "Blacklist", new String[0], "Item classes that appear in this list will not be repaired by the Reconstructor.");
		blacklist = new HashSet<String>(Arrays.asList(blProperty.getStringList()));
        if (config.hasChanged()) {
            config.save();
        }

        reconBlock = new BlockRecon();
        GameRegistry.register(reconBlock);
		GameRegistry.register(reconBlock.getItemBlock());

        MinecraftForge.EVENT_BUS.register(this);
    }

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ReconGuiHandler());
		GameRegistry.registerTileEntity(TileRecon.class, "Reconstructor");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		loadRecipes();
		if (proxy.isClient()) {
			proxy.doClientRegistrations();
		}
	}
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new AddBlacklistCommand());
	}
	
	private void loadRecipes() {
        Item keyItem = Item.REGISTRY.getObject(new ResourceLocation("ThermalExpansion","powerCoilGold"));
        if (keyItem == null) {
            keyItem = Items.REDSTONE;
        }
		ItemStack keyStack = new ItemStack(keyItem,1);
		ShapedOreRecipe gearRecipe = new ShapedOreRecipe(
				new ItemStack(reconBlock, 1), // output
				"iii", // (
				"iai", // Shaped pattern
				"gcg", // )
				'i', Items.IRON_INGOT,
				'a', Blocks.ANVIL,
				'g', (recipeItem.contains(":") ? Item.REGISTRY.getObject(new ResourceLocation(recipeItem)) : recipeItem),
				'c', keyStack
		);
		GameRegistry.addRecipe(gearRecipe);
	}

}
