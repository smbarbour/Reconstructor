package org.mcupdater.reconstructor.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.mcupdater.reconstructor.tile.TileRecon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryHelper
{
	public static boolean canStackFitInInventory(InvWrapper target, ItemStack toInsert) {
		if (target.getInv() == null || toInsert.isEmpty()) {
			return false;
		}
		ItemStack remainingItems = toInsert.copy();
		for (int slot = 0; slot < target.getSlots() && !remainingItems.isEmpty(); slot++ ) {
			remainingItems = target.insertItem(slot, remainingItems, true);
		}
		return (remainingItems.isEmpty());
	}

	public static ItemStack insertItemStackIntoInventory(InvWrapper target, ItemStack toInsert) {
		if (target.getInv() == null || toInsert.isEmpty()) {
			return toInsert;
		}
		for (int slot = 0; slot < target.getSlots() && !toInsert.isEmpty(); slot++ ) {
			toInsert = target.insertItem(slot, toInsert, false);
		}
		return toInsert;
	}

	public static boolean addToPriorityInventory(World world, BlockPos pos, ItemStack stack) {
		List<BlockPos> sides = getSideList(pos, ((TileRecon)world.getTileEntity(pos)).getOrientation());
		for (BlockPos side : sides) {
			TileEntity target;
			target = world.getTileEntity(side);
			if (target instanceof IInventory) {
				InvWrapper invOutput = new InvWrapper((IInventory) target);
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

	private static List<BlockPos> getSideList(BlockPos pos, EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return new ArrayList<BlockPos>(Arrays.asList(pos.down(), pos.west(), pos.south(), pos.east(), pos.up()));
			case EAST:
				return new ArrayList<BlockPos>(Arrays.asList(pos.down(), pos.north(), pos.west(), pos.south(), pos.up()));
			case SOUTH:
				return new ArrayList<BlockPos>(Arrays.asList(pos.down(), pos.east(), pos.north(), pos.west(), pos.up()));
			case WEST:
				return new ArrayList<BlockPos>(Arrays.asList(pos.down(), pos.south(), pos.east(), pos.north(), pos.up()));
		}
		return new ArrayList<BlockPos>(Arrays.asList(pos.down(), pos.south(), pos.east(), pos.north(), pos.up()));
	}
}
