package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.block.ReconstructorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(Registration.RECONSTRUCTOR_MENU.get(), ReconstructorScreen::new);
    }

}
