package com.mcupdater.reconstructor.item;

import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.block.ReconstructorEntity;
import com.mcupdater.reconstructor.setup.Config;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortableReconstructor extends Item {
	public PortableReconstructor(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		if (!pLevel.isClientSide()) {
			if (pStack.getOrDefault(Registration.COOLDOWN.get(), 0) == 0) {
				@Nullable IEnergyStorage energyStore = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
				if (energyStore.getEnergyStored() >= Config.ENERGY_PER_POINT.get()) {
					if (pEntity instanceof Player player) {
						ItemStack repairable = player.getInventory().armor.stream().filter(Reconstructor::canRepair).findFirst().orElse(ItemStack.EMPTY);
						if (!repairable.isEmpty()) {
							int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (pStack.getMaxDamage() / 1000)) : 1;
							repairable.setDamageValue(repairable.getDamageValue() - repairAmount);
							energyStore.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
							pStack.set(Registration.COOLDOWN.get(), Config.COOLDOWN.get());
							Reconstructor.LOGGER.debug("Repaired {} (armor): {} {}/{}", repairAmount, repairable.getItem().getDescriptionId(), repairable.getDamageValue(), repairable.getMaxDamage());
							return;
						}
						repairable = player.getInventory().offhand.stream().filter(Reconstructor::canRepair).findFirst().orElse(ItemStack.EMPTY);
						if (!repairable.isEmpty()) {
							int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (pStack.getMaxDamage() / 1000)) : 1;
							repairable.setDamageValue(repairable.getDamageValue() - repairAmount);
							energyStore.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
							pStack.set(Registration.COOLDOWN.get(), Config.COOLDOWN.get());
							Reconstructor.LOGGER.debug("Repaired {} (offhand): {} {}/{}", repairAmount, repairable.getItem().getDescriptionId(), repairable.getDamageValue(), repairable.getMaxDamage());
							return;
						}
						repairable = player.getInventory().getSelected();
						if (!repairable.isEmpty() && Reconstructor.canRepair(repairable)) {
							int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (pStack.getMaxDamage() / 1000)) : 1;
							repairable.setDamageValue(repairable.getDamageValue() - repairAmount);
							energyStore.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
							pStack.set(Registration.COOLDOWN.get(), Config.COOLDOWN.get());
							Reconstructor.LOGGER.debug("Repaired {} (selected): {} {}/{}", repairAmount, repairable.getItem().getDescriptionId(), repairable.getDamageValue(), repairable.getMaxDamage());
							return;
						}
						repairable = player.getInventory().items.stream().filter(Reconstructor::canRepair).findFirst().orElse(ItemStack.EMPTY);
						if (!repairable.isEmpty()) {
							int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (pStack.getMaxDamage() / 1000)) : 1;
							repairable.setDamageValue(repairable.getDamageValue() - repairAmount);
							energyStore.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
							pStack.set(Registration.COOLDOWN.get(), Config.COOLDOWN.get());
							Reconstructor.LOGGER.debug("Repaired {} (inventory): {} {}/{}", repairAmount, repairable.getItem().getDescriptionId(), repairable.getDamageValue(), repairable.getMaxDamage());
						}
					}
				}
			} else {
				pStack.set(Registration.COOLDOWN.get(), pStack.getOrDefault(Registration.COOLDOWN.get(),1)-1);
			}
		}
	}

	@Override
	public boolean isBarVisible(ItemStack pStack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack pStack) {
		@Nullable IEnergyStorage energyStore = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energyStore == null || energyStore.getMaxEnergyStored() == 0) return 13;

		return Math.min(13 * energyStore.getEnergyStored() / energyStore.getMaxEnergyStored(), 13);
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return Mth.color(1,0,0);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) return;

		IEnergyStorage energyStorage = pStack.getCapability(Capabilities.EnergyStorage.ITEM, null);
		if (energyStorage != null) {
			pTooltipComponents.add(Component.literal(String.format("%d / %d FE",energyStorage.getEnergyStored(),energyStorage.getMaxEnergyStored())));
		}
	}
}
