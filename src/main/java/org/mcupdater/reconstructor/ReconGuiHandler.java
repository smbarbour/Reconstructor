package org.mcupdater.reconstructor;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ReconGuiHandler implements IGuiHandler
{

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockRecon) {

			TileEntity tile = world.getTileEntity(pos);

			switch (ID) {
				case 0:
					if (!(tile instanceof TileRecon))
						return null;
					return new GuiReconstructor(player.inventory, world, (TileRecon) tile);

				default:
					return null;
			}
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockRecon) {

			TileEntity tile = world.getTileEntity(pos);

			switch (ID) {
				case 0:
					if (!(tile instanceof TileRecon))
						return null;
					return new ContainerRecon(player.inventory, (TileRecon) tile);

				default:
					return null;
			}
		}
		return null;
	}

}
