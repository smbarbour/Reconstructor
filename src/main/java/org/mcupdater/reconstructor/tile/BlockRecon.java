package org.mcupdater.reconstructor.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.mcupdater.reconstructor.Reconstructor;

public class BlockRecon extends BlockContainer
{
	public static final PropertyDirection FACING;

	static {
		FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	}

	private Item itemBlock;

	public BlockRecon() {
		super(Material.IRON);
		setUnlocalizedName("reconstructorblock");
		setRegistryName(Reconstructor.metadata.modId, "reconstructorblock");
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setHardness(10F);
		setResistance(10F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.REDSTONE);

		itemBlock = this.generateItemBlock();
	}

	private ItemBlock generateItemBlock() {
		ItemBlock itemBlock = new ItemBlockReconstructor(this);
		return itemBlock;
	}

	public Item getItemBlock() {
		return this.itemBlock;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, blockState, placer, stack);
		world.setBlockState(pos, blockState.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileRecon) {
			((TileRecon) tile).setOrientation(world.getBlockState(pos).getValue(FACING));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileRecon();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileRecon tile = (TileRecon) world.getTileEntity(pos);
		
		if (player.isSneaking())
			return false;
		
		if (tile != null) {
			if (!world.isRemote) {
				player.openGui(Reconstructor.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumFacing = EnumFacing.getFront(meta);
		if(enumFacing.getAxis() == EnumFacing.Axis.Y) {
			enumFacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumFacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	private class ItemBlockReconstructor extends ItemBlock {

		public ItemBlockReconstructor(Block block) {
			super(block);
			setUnlocalizedName("reconstructorblock");
			setRegistryName(Reconstructor.metadata.modId, "reconstructorblock");
		}
	}
}
