package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.block.ReconstructorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(value=Dist.CLIENT, modid= Reconstructor.MODID, bus=EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == com.mcupdater.mculib.setup.MCULibRegistration.ITEM_GROUP.get()) {
            event.accept(Registration.RECONSTRUCTOR_BLOCK.get());
            event.accept(Registration.PORTABLE_RECONSTRUCTOR.get());
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.RECONSTRUCTOR_MENU.get(), ReconstructorScreen::new);
    }
}
