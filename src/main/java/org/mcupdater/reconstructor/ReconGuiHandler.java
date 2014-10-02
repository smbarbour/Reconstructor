package org.mcupdater.reconstructor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class ReconGuiHandler implements IGuiHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z))
			return null;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		switch (ID) {
		case 0:
			if (!(tile instanceof TileRecon))
				return null;
			return new GuiReconstructor(player.inventory, world, (TileRecon) tile);

		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		if (!world.blockExists(x, y, z))
			return null;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		switch (ID) {
		case 0:
			if (!(tile instanceof TileRecon))
				return null;
			return new ContainerRecon(player.inventory, (TileRecon) tile);
			
		default:
			return null;
		}

	}

}
