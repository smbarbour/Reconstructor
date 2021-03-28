package com.mcupdater.reconstructor.network;

import com.mcupdater.reconstructor.tile.TileRecon;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AutoEjectPacket {
    private BlockPos blockPos;

    public AutoEjectPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public static void toBytes(AutoEjectPacket msg, PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(msg.blockPos);
    }

    public static AutoEjectPacket fromBytes(PacketBuffer packetBuffer) {
        return new AutoEjectPacket(packetBuffer.readBlockPos());
    }

    public static void handle(AutoEjectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = ctx.get().getSender().level;
            if (world.getBlockEntity(msg.blockPos) instanceof TileRecon) {
                TileRecon tileRecon = (TileRecon) world.getBlockEntity(msg.blockPos);
                tileRecon.data.set(tileRecon.data.get()-1);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
