package com.mcupdater.reconstructor.block;

import com.mcupdater.mculib.gui.WidgetPower;
import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.network.AutoEjectPacket;
import com.mcupdater.reconstructor.network.ReconstructorChannel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReconstructorScreen extends AbstractContainerScreen<ReconstructorMenu> {
    private ResourceLocation GUI = new ResourceLocation(Reconstructor.MODID, "textures/gui/recon.png");
    private Button btnAutoEject;

    public ReconstructorScreen(ReconstructorMenu container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new WidgetPower(this.leftPos + 153, this.topPos + 5, 18, 71, menu.getEnergyHandler(), WidgetPower.Orientation.VERTICAL));
        btnAutoEject = this.addRenderableWidget(new Button(this.leftPos + 55, this.topPos + 5, 95, 20, new TextComponent(""), buttonPress -> {
            ReconstructorChannel.INSTANCE.sendToServer(new AutoEjectPacket(menu.getBlockEntity().getBlockPos()));
            menu.broadcastChanges();
        }));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY); //renderHoveredToolTip
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // Do not draw normal container labels
        btnAutoEject.setMessage(new TextComponent("Auto-eject: " + (menu.getBlockEntity().isAutoEject() ? "ON" : "OFF")));
    }

    //renderBackground
    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        //this.minecraft.getTextureManager().getTexture(GUI);
        int relX = this.leftPos;
        int relY = this.topPos;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

}
