package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.tile.ScreenRecon;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.RECONBLOCK_CONTAINER.get(), ScreenRecon::new);
    }
}
