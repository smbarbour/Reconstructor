package com.mcupdater.reconstructor.network;

import com.mcupdater.reconstructor.Reconstructor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ReconstructorChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reconstructor.MODID, "autoeject"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

        INSTANCE.registerMessage(0,
                AutoEjectPacket.class,
                AutoEjectPacket::toBytes,
                AutoEjectPacket::fromBytes,
                AutoEjectPacket::handle
        );
    }
}
