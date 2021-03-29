package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.gui.WidgetPower;
import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.network.AutoEjectPacket;
import com.mcupdater.reconstructor.network.ReconstructorChannel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRecon extends ContainerScreen<ContainerRecon> {
    private ResourceLocation GUI = new ResourceLocation(Reconstructor.MODID, "textures/gui/recon.png");
    private Button btnAutoEject;

    public ScreenRecon(ContainerRecon container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.passEvents = false;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new WidgetPower(this.leftPos + 153, this.topPos + 5, 18, 71, menu.getEnergyHandler(), WidgetPower.Orientation.VERTICAL));
        btnAutoEject = this.addButton(new Button(this.leftPos + 55, this.topPos + 5, 95, 20, new StringTextComponent(""), buttonPress -> {
            ReconstructorChannel.INSTANCE.sendToServer(new AutoEjectPacket(menu.getBlockEntity().getBlockPos()));
            menu.broadcastChanges();
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY); //renderHoveredToolTip
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Do not draw normal container labels
        btnAutoEject.setMessage(new StringTextComponent("Auto-eject: " + (menu.getBlockEntity().isAutoEject() ? "ON" : "OFF")));
    }

    //renderBackground
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = this.leftPos;
        int relY = this.topPos;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

}
