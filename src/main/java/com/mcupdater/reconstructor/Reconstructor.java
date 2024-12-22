package com.mcupdater.reconstructor;

import com.mcupdater.reconstructor.setup.ClientSetup;
import com.mcupdater.reconstructor.setup.Config;
import com.mcupdater.reconstructor.setup.ModSetup;
import com.mcupdater.reconstructor.setup.Registration;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reconstructor.MODID)
public class Reconstructor
{
    public static final String MODID = "reconstructor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Reconstructor(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Registration.init(modEventBus);

        modEventBus.addListener(ModSetup::init);
    }

}
