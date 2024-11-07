package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.Reconstructor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Reconstructor.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {
        //ReconstructorChannel.init();
    }
}
