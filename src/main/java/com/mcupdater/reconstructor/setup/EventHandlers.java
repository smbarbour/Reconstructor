package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.block.ReconstructorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid= Reconstructor.MODID, bus= EventBusSubscriber.Bus.MOD)
public class EventHandlers {

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		Reconstructor.LOGGER.info("Registering capabilities");
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				Registration.RECONSTRUCTOR_ENTITY.get(),
				(blockEntity, side) -> side != null ? blockEntity.getEnergyStorage().getEnergyHandler(side) : blockEntity.getEnergyStorage().getInternalHandler());
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				Registration.RECONSTRUCTOR_ENTITY.get(),
				(blockEntity, side) -> side != null ? blockEntity.getItemHandler().getItemHandler(side) : blockEntity.getItemHandler().getInternalHandler());
	}
}
