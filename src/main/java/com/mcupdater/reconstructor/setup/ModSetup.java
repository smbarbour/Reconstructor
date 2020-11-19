package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.Reconstructor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Reconstructor.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {
    public static final ItemGroup ITEM_GROUP = new ItemGroup("reconstructor") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.RECONBLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {}
}
