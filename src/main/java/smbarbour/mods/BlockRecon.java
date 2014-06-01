package smbarbour.mods;

import buildcraft.api.core.Position;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import smbarbour.mods.shared.BCInteract;
import smbarbour.mods.shared.Utils;

public class BlockRecon extends BlockContainer {

	IIcon textureTop;
	IIcon textureFront;
	IIcon textureSide;
	
	public BlockRecon() {
		super(Material.iron);
		setHardness(10F);
		setResistance(10F);
		setStepSound(soundTypeMetal);
		setBlockName("reconstructorBlock");
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);

		ForgeDirection orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ), new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.getOpposite().ordinal(),1);
	}

	@Override
	public IIcon getIcon(int i, int j) {
		// If no metadata is set, then this is an icon.
		if (j == 0 && i == 3)
			return textureFront;

		if (i == j && i>1) // Front can't be top or bottom.
			return textureFront;

		switch (i) {
		case 1:
			return textureTop;
		default:
			return textureSide;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileRecon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegistry)
	{
	    textureFront = iconRegistry.registerIcon("reconstructor:reconstructor_front");
        textureSide = iconRegistry.registerIcon("reconstructor:reconstructor_side");
        textureTop = iconRegistry.registerIcon("reconstructor:reconstructor_top");
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7, float par8, float par9) {
		TileRecon tile = (TileRecon) world.getTileEntity(i, j, k);
		
		if (player.isSneaking())
			return false;
		
		if (Reconstructor.instance.doPipeInteract) {
			if (BCInteract.isHoldingPipe(player)) {
				return false;
			}
		}
		
		if (tile != null) {
			return tile.onBlockActivated(player, ForgeDirection.getOrientation(side));
		}
		
		return false;
	}

}
