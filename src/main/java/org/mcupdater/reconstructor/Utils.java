package org.mcupdater.reconstructor;

/*
 * This code is derived from the Buildcraft class of the same name as it appeared in Buildcraft 4.2.2
 */

import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.mcupdater.reconstructor.InventoryIterator.IInvSlot;

import java.util.*;

public class Utils {
	public static final Random rng = new Random();
	private static final List<ForgeDirection> directions = new ArrayList<ForgeDirection>(Arrays.asList(ForgeDirection.VALID_DIRECTIONS));

	public static ForgeDirection get2dOrientation(Position pos1, Position pos2) {
		double Dx = pos1.x - pos2.x;
		double Dz = pos1.z - pos2.z;
		double angle = Math.atan2(Dz, Dx) / Math.PI * 180 + 180;

		if (angle < 45 || angle > 315) {
			return ForgeDirection.EAST;
		} else if (angle < 135) {
			return ForgeDirection.SOUTH;
		} else if (angle < 225) {
			return ForgeDirection.WEST;
		} else {
			return ForgeDirection.NORTH;
		}
	}

	public static int addToPriorityInventory(World world, int x, int y, int z, ItemStack stack) {
		int meta = world.getBlockMetadata(x,y,z);
		List<Integer> sides = new ArrayList<Integer>(Arrays.asList(BlockHelper.getBelowSide(meta),BlockHelper.getRightSide(meta),BlockHelper.getOppositeSide(meta),BlockHelper.getLeftSide(meta),BlockHelper.getAboveSide(meta)));
		for (Integer side : sides) {
			TileEntity target;
			target = BlockHelper.getAdjacentTileEntity(world, x, y, z, side.intValue());
			if (target instanceof ISidedInventory || target instanceof IInventory) {
				if (add((IInventory) target, stack, ForgeDirection.values()[BlockHelper.getOppositeSide(side)], true).stackSize > 0) {
					return add((IInventory) target, stack, ForgeDirection.values()[BlockHelper.getOppositeSide(side)], false).stackSize;
				}
			}
		}
		return 0;
	}

	private static ItemStack add(IInventory inventory, ItemStack stack, ForgeDirection side, boolean simulate) {
		ItemStack added = stack.copy();
		added.stackSize = inject(inventory, stack, side, simulate);
		return added;
	}

	private static int inject(IInventory inventory, ItemStack stack, ForgeDirection side, boolean simulate) {
		List<IInvSlot> filledSlots = new ArrayList<IInvSlot>(inventory.getSizeInventory());
		List<IInvSlot> emptySlots = new ArrayList<IInvSlot>(inventory.getSizeInventory());
		for (IInvSlot slot : InventoryIterator.getIterable(inventory, side)) {
			if (slot.canPutStackInSlot(stack)) {
				if (slot.getStackInSlot() == null) {
					emptySlots.add(slot);
				} else {
					filledSlots.add(slot);
				}
			}
		}

		int injected = 0;
		injected = tryPut(inventory, stack, filledSlots, injected, simulate);
		injected = tryPut(inventory, stack, emptySlots, injected, simulate);

		inventory.markDirty();
		return injected;
	}

	private static int tryPut(IInventory inventory, ItemStack stack, List<IInvSlot> slots, int injected, boolean simulate) {
		if (injected >= stack.stackSize) {
			return injected;
		}
		for (IInvSlot slot : slots) {
			ItemStack stackInSlot = slot.getStackInSlot();
			if (stackInSlot == null || StackHelper.instance().canStacksMerge(stackInSlot, stack)) {
				int used = addToSlot(inventory, slot, stack, injected, simulate);
				if (used > 0) {
					injected += used;
					if (injected >= stack.stackSize) {
						return injected;
					}
				}
			}
		}
		return injected;
	}

	private static int addToSlot(IInventory inventory, IInvSlot slot, ItemStack stack, int injected, boolean simulate) {
		int available = stack.stackSize - injected;
		int max = Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit());

		ItemStack stackInSlot = slot.getStackInSlot();
		if (stackInSlot == null) {
			int wanted = Math.min(available, max);
			if (!simulate) {
				stackInSlot = stack.copy();
				stackInSlot.stackSize = wanted;
				slot.setStackInSlot(stackInSlot);
			}
			return wanted;
		}
		if (!StackHelper.instance().canStacksMerge(stack, stackInSlot)) {
			return 0;
		}
		int wanted = max - stackInSlot.stackSize;
		if (wanted <= 0) {
			return 0;
		}
		if (wanted > available) {
			wanted = available;
		}
		if (!simulate) {
			stackInSlot.stackSize += wanted;
			slot.setStackInSlot(stackInSlot);
		}
		return wanted;
	}
}
