package com.mcupdater.reconstructor;

import com.mcupdater.reconstructor.setup.ClientSetup;
import com.mcupdater.reconstructor.setup.Config;
import com.mcupdater.reconstructor.setup.ModSetup;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("reconstructor")
public class Reconstructor
{
    public static final String MODID = "reconstructor";
    public static final Logger LOGGER = LogManager.getLogger();

    public Reconstructor() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Registration.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModSetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
    }



}
