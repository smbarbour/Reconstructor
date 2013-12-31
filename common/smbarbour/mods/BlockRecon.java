package smbarbour.mods;

import java.util.ArrayList;

import buildcraft.core.BlockBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import buildcraft.api.core.Position;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.IItemPipe;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRecon extends BlockBuildCraft {

	Icon textureTop;
	Icon textureFront;
	Icon textureSide;
	
	public BlockRecon(int par1) {
		super(par1, Material.iron);
		setHardness(10F);
		setResistance(10F);
		setStepSound(soundMetalFootstep);
		setUnlocalizedName("reconstructorBlock");
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);

		ForgeDirection orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ), new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.getOpposite().ordinal(),1);
	}

	@Override
	public Icon getIcon(int i, int j) {
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
	public TileEntity createNewTileEntity(World world) {
		return new TileRecon();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistry)
	{
	    textureFront = iconRegistry.registerIcon("reconstructor:reconstructor_front");
        textureSide = iconRegistry.registerIcon("reconstructor:reconstructor_side");
        textureTop = iconRegistry.registerIcon("reconstructor:reconstructor_top");
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7, float par8, float par9) {
		TileRecon tile = (TileRecon) world.getBlockTileEntity(i, j, k);
		
		if (player.isSneaking())
			return false;
		
		if (player.getCurrentEquippedItem() != null) {
			if (player.getCurrentEquippedItem().getItem() instanceof IItemPipe) {
				return false;
			}
		}
		
		if (tile instanceof TileRecon) {
			return ((TileRecon) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
		}
		
		return false;
	}

}
