package org.mcupdater.reconstructor;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class Config {
	public static Configuration config;
	public static int energyPerPoint;
	public static Set<String> blacklist;
	public static Property blProperty;
	public static boolean restrictRepairs;
	public static boolean scaledRepair;
	public static boolean debug;
	public static Property wlProperty;
	public static Set<String> whitelist;

	public static void init(File configFile) {
		config = new Configuration(configFile);
		config.load();
		config.renameProperty("General","RF_per_damage_point","EnergyUse");
		config.getCategory("General").remove("Recipe_Item");
		energyPerPoint = config.get("General", "EnergyUse", 50, "How much energy is required per point of damage.").getInt();
		restrictRepairs = config.get("General", "Restricted", false, "If true, will only repair things that extend the tool, armor, sword and bow classes.").getBoolean();
		scaledRepair = config.get("General", "Scaled", true, "Repair amount per tick will scale based on durability").getBoolean();
		debug = config.get("General","Debug", false, "Write class and reason info to log when attempting to repair an item").getBoolean();
		blProperty = config.get("General", "Blacklist", new String[0], "Item classes that appear in this list will not be repaired by the Reconstructor.");
		blacklist = new HashSet<String>(Arrays.asList(blProperty.getStringList()));
		wlProperty = config.get("General", "Whitelist", new String[]{"slimeknights.tconstruct.tools","landmaster.plustic.tools","c4.conarm.common.items.armor"}, "Package names of mods that should be repaired by the Reconstructor.");
		whitelist = new HashSet<String>(Arrays.asList(wlProperty.getStringList()));
		if (config.hasChanged()) {
			config.save();
		}

	}

	@SubscribeEvent
	public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		config.save();
	}
}
