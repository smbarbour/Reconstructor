package com.mcupdater.reconstructor.item;

import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public class ItemEnergyStorage extends EnergyStorage {
	protected final ItemStack stack;

	public ItemEnergyStorage(ItemStack stack, int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
		this.stack = stack;
		this.energy = stack.getOrDefault(Registration.STORED_ENERGY,0);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = super.receiveEnergy(maxReceive, simulate);
		if (!simulate) {
			stack.set(Registration.STORED_ENERGY, this.energy);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = super.extractEnergy(maxExtract, simulate);
		if (!simulate) {
			stack.set(Registration.STORED_ENERGY, this.energy);
		}
		return energyExtracted;
	}
}
