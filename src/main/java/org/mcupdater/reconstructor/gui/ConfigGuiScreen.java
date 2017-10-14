package org.mcupdater.reconstructor.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.mcupdater.reconstructor.Config;
import org.mcupdater.reconstructor.Reconstructor;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ConfigGuiScreen extends GuiConfig {
	public ConfigGuiScreen(final GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), Reconstructor.metadata.modId, "reconconfig", false, false, "Reconstructor Config");
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> configElements = new ArrayList<>();
		configElements.addAll(new ConfigElement(Config.config.getCategory("General")).getChildElements());
		return configElements;
	}
}
