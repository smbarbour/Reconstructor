package org.mcupdater.reconstructor.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import org.mcupdater.reconstructor.Reconstructor;

public class ClientProxy extends CommonProxy
{
	public ClientProxy() {
		this.client = true;
	}

	@Override
	public void doClientRegistrations() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(Reconstructor.reconBlock), 0, new ModelResourceLocation("reconstructor:reconstructorBlock", "inventory"));
	}
}
