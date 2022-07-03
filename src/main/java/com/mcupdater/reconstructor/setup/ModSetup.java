package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.network.ReconstructorChannel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Reconstructor.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("reconstructor") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.RECONSTRUCTOR_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {
        ReconstructorChannel.init();
    }
}
