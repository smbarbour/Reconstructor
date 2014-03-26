package smbarbour.mods;

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
