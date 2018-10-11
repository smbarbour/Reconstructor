package org.mcupdater.reconstructor;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import org.mcupdater.reconstructor.gui.ReconGuiHandler;
import org.mcupdater.reconstructor.proxy.CommonProxy;
import org.mcupdater.reconstructor.tile.TileRecon;

@Mod(modid = "reconstructor", useMetadata = true, guiFactory = "org.mcupdater.reconstructor.gui.GuiFactory")
public class Reconstructor {

	@SidedProxy(clientSide = "org.mcupdater.reconstructor.proxy.ClientProxy", serverSide = "org.mcupdater.reconstructor.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static ModMetadata metadata;
	@Mod.Instance("reconstructor")
	public static Reconstructor instance;
	private Logger logger;

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
		metadata = evt.getModMetadata();
		Config.init(evt.getSuggestedConfigurationFile());
		this.logger = evt.getModLog();
    }

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ReconGuiHandler());
		GameRegistry.registerTileEntity(TileRecon.class, "reconstructor");
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new AddBlacklistCommand());
	}


	public Logger getLogger() {
		return logger;
	}
}
