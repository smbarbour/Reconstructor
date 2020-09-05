package org.mcupdater.reconstructor.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.mcupdater.reconstructor.tile.TileRecon;

import javax.annotation.Nonnull;

public class ReconInvWrapper extends InvWrapper {
    private final TileRecon tile;

    public ReconInvWrapper(TileRecon tile) {
        super(tile);
        this.tile = tile;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!this.tile.isExtractable()) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }

}
