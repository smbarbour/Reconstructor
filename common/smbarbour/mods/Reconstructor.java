package smbarbour.mods;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.core.ItemBlockBuildCraft;
import buildcraft.core.proxy.CoreProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "Reconstructor", name="Reconstructor", version="1.2", acceptedMinecraftVersions="[1.6,1.7],", dependencies = "required-after:BuildCraft|Core")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Reconstructor {
	public static Configuration config;
	public static BlockRecon reconBlock;
	public static int energyPerPoint;
	@Instance("Reconstructor")
	public static Reconstructor instance;
	
	@EventHandler
	public void load(FMLInitializationEvent evt) {
		NetworkRegistry.instance().registerGuiHandler(instance, new ReconGuiHandler());
		CoreProxy.proxy.registerTileEntity(TileRecon.class, "Reconstructor");
		loadRecipes();
	}
	
	@EventHandler
	public void initialize(FMLPreInitializationEvent evt) {
		config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		int reconId = config.getBlock("reconstructor.id",3000).getInt(3000);
		energyPerPoint = config.get("General", "MJ_per_damage_point", 5).getInt(5);
		if (config.hasChanged()) {
			config.save();
		}
		
		reconBlock = new BlockRecon(reconId);
		GameRegistry.registerBlock(reconBlock, ItemBlockReconstructor.class, reconBlock.getUnlocalizedName().replace("tile.", ""));
		LanguageRegistry.addName(new ItemStack(reconBlock), "Reconstructor");
		
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void loadRecipes() {
		CoreProxy.proxy.addCraftingRecipe(new ItemStack(reconBlock,1),
				"iii",
				"iai",
				"gpg",
				'i', Item.ingotIron,
				'a', Block.anvil,
				'g', BuildCraftCore.diamondGearItem,
				'p', BuildCraftTransport.pipeItemsStone);
	}

}
