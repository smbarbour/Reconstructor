package smbarbour.mods;

import buildcraft.core.gui.BuildCraftContainer;
import buildcraft.core.gui.slots.SlotValidated;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerRecon extends BuildCraftContainer {

	private TileRecon tile;

	public ContainerRecon(InventoryPlayer inventoryplayer, TileRecon t) {
		super(1);
		this.tile = t;
		addSlotToContainer(new Slot(t, 0, 80, 41));
		
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
		return tile.isUseableByPlayer(entityplayer);
	}

}
