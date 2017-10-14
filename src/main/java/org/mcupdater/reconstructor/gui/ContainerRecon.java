package org.mcupdater.reconstructor.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.mcupdater.reconstructor.tile.TileRecon;
import org.mcupdater.reconstructor.helpers.StackHelper;

public class ContainerRecon extends Container {

	private TileRecon tile;
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	public ContainerRecon(final InventoryPlayer inventoryplayer, TileRecon t) {
		this.tile = t;
		addSlotToContainer(new Slot(t, 0, 80, 41));

        for (int i = 0; i < 4; ++i)
        {
	        final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[i];
            this.addSlotToContainer(new Slot(inventoryplayer, 36 + (3 - i), 8, 8 + i * 18)
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack itemStack)
                {
                    if (itemStack.isEmpty()) return false;
                    return itemStack.getItem().isValidArmor(itemStack, entityequipmentslot, inventoryplayer.player);
                }

	            public String getSlotTexture()
	            {
		            return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
	            }
            });
        }

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
			}
		}
		
		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUsableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack originalStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		int numSlots = inventorySlots.size();
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();
			if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots)) {
// NOOP
			} else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
				if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
				if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
					return ItemStack.EMPTY;
				}
			} else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots)) {
				return ItemStack.EMPTY;
			}
			slot.onSlotChange(stackInSlot, originalStack);
			if (stackInSlot.getCount() <= 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (stackInSlot.getCount() == originalStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, stackInSlot);
		}
		return originalStack;
	}

	protected boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				Slot slot = inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (!stackInSlot.isEmpty() && StackHelper.instance().canStacksMerge(stackInSlot, stackToShift)) {
					int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					if (resultingStackSize <= max) {
						stackToShift.setCount(0);
						stackInSlot.setCount(resultingStackSize);
						slot.onSlotChanged();
						changed = true;
					} else if (stackInSlot.getCount() < max) {
						stackToShift.setCount(stackToShift.getCount() - max - stackInSlot.getCount());
						stackInSlot.setCount(max);
						slot.onSlotChanged();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.getCount() > 0) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				Slot slot = inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot.isEmpty()) {
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					stackInSlot = stackToShift.copy();
					stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
					stackToShift.setCount(stackToShift.getCount() - stackInSlot.getCount());
					slot.putStack(stackInSlot);
					slot.onSlotChanged();
					changed = true;
				}
			}
		}
		return changed;
	}

	private boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
			Slot slot = inventorySlots.get(machineIndex);
			if (!slot.isItemValid(stackToShift)) {
				continue;
			}
			if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1)) {
				return true;
			}
		}
		return false;
	}
}
