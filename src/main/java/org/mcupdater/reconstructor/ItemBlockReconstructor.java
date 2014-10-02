package org.mcupdater.reconstructor;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReconstructor extends ItemBlock {

	public ItemBlockReconstructor(Block block) {
		super(block);
	}

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return "tile.reconstructor";
    }
}
