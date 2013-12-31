package smbarbour.mods;

import buildcraft.core.utils.StringUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReconstructor extends ItemBlock {

	public ItemBlockReconstructor(int id) {
		super(id);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return "Reconstructor";
	}

}
