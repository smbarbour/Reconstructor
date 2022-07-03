package com.mcupdater.reconstructor.network;

import com.mcupdater.reconstructor.block.ReconstructorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AutoEjectPacket {
    private BlockPos blockPos;

    public AutoEjectPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public static void toBytes(AutoEjectPacket msg, FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.blockPos);
    }

    public static AutoEjectPacket fromBytes(FriendlyByteBuf packetBuffer) {
        return new AutoEjectPacket(packetBuffer.readBlockPos());
    }

    public static void handle(AutoEjectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level;
            if (level.getBlockEntity(msg.blockPos) instanceof ReconstructorEntity) {
                ReconstructorEntity reconstructorEntity = (ReconstructorEntity) level.getBlockEntity(msg.blockPos);
                reconstructorEntity.data.set(reconstructorEntity.data.get()-1);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
