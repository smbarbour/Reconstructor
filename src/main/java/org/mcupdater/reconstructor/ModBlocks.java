package org.mcupdater.reconstructor;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.mcupdater.reconstructor.tile.BlockRecon;

public class ModBlocks {
	@GameRegistry.ObjectHolder("reconstructor:reconstructorblock")
	public static BlockRecon reconstructor;

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		reconstructor.initModel();
	}
}
