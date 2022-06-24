package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.tile.ScreenRecon;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(Registration.RECONBLOCK.get(), RenderType.cutoutMipped());
        MenuScreens.register(Registration.RECONBLOCK_CONTAINER.get(), ScreenRecon::new);
    }

}
