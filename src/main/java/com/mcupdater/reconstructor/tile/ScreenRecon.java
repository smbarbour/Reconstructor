package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.gui.WidgetPower;
import com.mcupdater.reconstructor.Reconstructor;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRecon extends ContainerScreen<ContainerRecon> {
    private ResourceLocation GUI = new ResourceLocation(Reconstructor.MODID, "textures/gui/recon.png");

    public ScreenRecon(ContainerRecon container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.passEvents = false;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new WidgetPower(this.guiLeft + 153, this.guiTop + 5, 18, 71, container.getEnergyHandler(), WidgetPower.Orientation.VERTICAL));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY); //renderHoveredToolTip
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Do not draw normal container labels
    }

    //renderBackground
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = this.guiLeft;
        int relY = this.guiTop;
        this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
    }

}
