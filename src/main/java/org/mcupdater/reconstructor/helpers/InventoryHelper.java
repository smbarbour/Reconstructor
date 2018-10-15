package org.mcupdater.reconstructor.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.mcupdater.reconstructor.tile.TileRecon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryHelper
{
	public static boolean canStackFitInInventory(IItemHandler target, ItemStack toInsert) {
		if (toInsert.isEmpty()) {
			return false;
		}
		ItemStack remainingItems = toInsert.copy();
		for (int slot = 0; slot < target.getSlots() && !remainingItems.isEmpty(); slot++ ) {
			remainingItems = target.insertItem(slot, remainingItems, true);
		}
		return (remainingItems.isEmpty());
	}

	public static ItemStack insertItemStackIntoInventory(IItemHandler target, ItemStack toInsert) {
		if (toInsert.isEmpty()) {
			return toInsert;
		}
		for (int slot = 0; slot < target.getSlots() && !toInsert.isEmpty(); slot++ ) {
			toInsert = target.insertItem(slot, toInsert, false);
		}
		return toInsert;
	}

	public static IItemHandler getWrapper(TileEntity tileEntity, EnumFacing side) {
		if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		} else if (tileEntity instanceof ISidedInventory) {
			return new SidedInvWrapper((ISidedInventory) tileEntity, side);
		} else if (tileEntity instanceof IInventory) {
			return new InvWrapper((IInventory) tileEntity);
		}
		return EmptyHandler.INSTANCE;
	}

	public static boolean addToPriorityInventory(World world, BlockPos pos, ItemStack stack) {
		List<EnumFacing> sides = getSideList(pos, ((TileRecon)world.getTileEntity(pos)).getOrientation());
		for (EnumFacing side : sides) {
			TileEntity target;
			target = world.getTileEntity(pos.offset(side));
			if (target instanceof IInventory) {
				IItemHandler invOutput = getWrapper(target, side.getOpposite());
				if (canStackFitInInventory(invOutput, stack)) {
					ItemStack remain = insertItemStackIntoInventory(invOutput, stack);
					if (remain.isEmpty()) {
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	private static List<EnumFacing> getSideList(BlockPos pos, EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return new ArrayList<>(Arrays.asList(EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.UP));
			case EAST:
				return new ArrayList<>(Arrays.asList(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP));
			case SOUTH:
				return new ArrayList<>(Arrays.asList(EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.UP));
			case WEST:
				return new ArrayList<>(Arrays.asList(EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.UP));
		}
		return new ArrayList<>(Arrays.asList(EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.UP));
	}
}
