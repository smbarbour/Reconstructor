package org.mcupdater.reconstructor.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.mcupdater.reconstructor.tile.TileRecon;

public class GuiReconstructor extends GuiContainer {

	public static final ResourceLocation gui = new ResourceLocation("reconstructor","textures/gui/recon.png");
	private TileRecon machine;

	public GuiReconstructor(InventoryPlayer inventoryplayer, World world, TileRecon tile) {
		super(new ContainerRecon(inventoryplayer,tile));
		this.machine = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(gui);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
