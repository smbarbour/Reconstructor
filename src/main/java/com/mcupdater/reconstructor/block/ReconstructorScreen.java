package com.mcupdater.reconstructor.block;

import com.mcupdater.mculib.block.AbstractMachineScreen;
import com.mcupdater.reconstructor.Reconstructor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReconstructorScreen extends AbstractMachineScreen<ReconstructorEntity,ReconstructorMenu> {
    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Reconstructor.MODID, "textures/gui/recon.png");

    public ReconstructorScreen(ReconstructorMenu container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected ResourceLocation getGUIResourceLocation() {
        return this.GUI;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
       // Don't render default labels
    }
}
