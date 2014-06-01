package smbarbour.mods.shared;

import buildcraft.core.IItemPipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BCInteract
{
	public static int addToPipe(World worldObj, int xCoord, int yCoord, int zCoord, ForgeDirection from, ItemStack stackInSlot) {
		return buildcraft.core.utils.Utils.addToRandomPipeAround(worldObj, xCoord, yCoord, zCoord, from, stackInSlot);
	}

	public static boolean isHoldingPipe(EntityPlayer player) {
        return player.getCurrentEquippedItem() != null && (player.getCurrentEquippedItem().getItem() instanceof IItemPipe);
	}
}
