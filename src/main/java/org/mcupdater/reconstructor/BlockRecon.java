package org.mcupdater.reconstructor;

import cofh.api.block.IDismantleable;
import cofh.lib.util.helpers.BlockHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public class BlockRecon extends BlockContainer implements IDismantleable
{
	IIcon textureFront;
	IIcon textureSide;
	
	public BlockRecon() {
		super(Material.iron);
		setHardness(10F);
		setResistance(10F);
		setStepSound(soundTypeMetal);
		setBlockName("reconstructorBlock");
		setCreativeTab(CreativeTabs.tabRedstone);
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

		return textureSide;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileRecon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegistry)
	{
	    textureFront = iconRegistry.registerIcon("reconstructor:framed_iron_hammer");
        textureSide = iconRegistry.registerIcon("reconstructor:framed_iron");
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7, float par8, float par9) {
		TileRecon tile = (TileRecon) world.getTileEntity(i, j, k);
		
		if (player.isSneaking())
			return false;
		
		if (tile != null) {
			return tile.onBlockActivated(player, ForgeDirection.getOrientation(side));
		}
		
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		int meta = world.getBlockMetadata(x,y,z);
		int newMeta = BlockHelper.getLeftSide(meta);
		world.setBlockMetadataWithNotify(x,y,z,newMeta,3);
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer entityPlayer, World world, int x, int y, int z, boolean placeInInventory) {
		ArrayList<ItemStack> dropped = new ArrayList<ItemStack>();
		dropped.add(new ItemStack(Reconstructor.reconBlock));
		world.setBlockToAir(x,y,z);

		//Spawn in world
		float multiplier = 0.3F;
		double deltaX = world.rand.nextFloat() * multiplier + (1.0F - multiplier) * 0.5D;
		double deltaY = world.rand.nextFloat() * multiplier + (1.0F - multiplier) * 0.5D;
		double deltaZ = world.rand.nextFloat() * multiplier + (1.0F - multiplier) * 0.5D;
		EntityItem spawnedItem = new EntityItem(world, x+deltaX, y+deltaY, z+ deltaZ, dropped.get(0));
		spawnedItem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(spawnedItem);

		return dropped;
	}

	@Override
	public boolean canDismantle(EntityPlayer entityPlayer, World world, int i, int i2, int i3) {
		return true;
	}
}
