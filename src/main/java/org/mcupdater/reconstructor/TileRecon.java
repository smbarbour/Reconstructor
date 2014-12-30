package org.mcupdater.reconstructor;

import cofh.api.energy.TileEnergyHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRecon extends TileEnergyHandler implements ISidedInventory
{
	private final BasicInventory inv;

	public TileRecon(){
		inv = new BasicInventory(1,"Processing",1);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (storage.getEnergyStored() > Reconstructor.energyPerPoint) {
			if (tryRepair()) {
				storage.extractEnergy(Reconstructor.energyPerPoint, false);
			}
		}
	}

	public boolean tryRepair() {
		if (getStackInSlot(0) == null)
			return false;
			
		if (!getStackInSlot(0).isItemDamaged() || !getStackInSlot(0).getItem().isRepairable() || Reconstructor.blacklist.contains(getStackInSlot(0).getItem().getUnlocalizedName()) || (Reconstructor.instance.restrictRepairs && !(getStackInSlot(0).getItem() instanceof ItemTool || getStackInSlot(0).getItem() instanceof ItemArmor || getStackInSlot(0).getItem() instanceof ItemSword || getStackInSlot(0).getItem() instanceof ItemBow))) {
			ejectItem();
			return false;
		}
		getStackInSlot(0).damageItem(-1, new GremlinEntity(this.worldObj));
		return true;
	}

	private void ejectItem() {
		if (Utils.addToPriorityInventory(worldObj, xCoord, yCoord, zCoord, getStackInSlot(0)) > 0) {
			decrStackSize(0, 1);
			return;
		}
		
		float f = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityitem = new EntityItem(this.worldObj, xCoord + f, yCoord + f1 + 0.5F, zCoord + f2, getStackInSlot(0));

		float f3 = 0.05F;
		entityitem.motionX = (float) this.worldObj.rand.nextGaussian() * f3;
		entityitem.motionY = (float) this.worldObj.rand.nextGaussian() * f3 + 1.0F;
		entityitem.motionZ = (float) this.worldObj.rand.nextGaussian() * f3;
		this.worldObj.spawnEntityInWorld(entityitem);
		
		decrStackSize(0, 1);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inv.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(slot, itemstack);
	}

	@Override
	public String getInventoryName() {
		return "Reconstructor";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem().isDamageable();
	}

	public boolean onBlockActivated(EntityPlayer player, ForgeDirection orientation) {
		if (!worldObj.isRemote) {
			player.openGui(Reconstructor.instance, 0, worldObj, xCoord, yCoord, zCoord);
		}
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		inv.readFromNBT(data);
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		inv.writeToNBT(data);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{0};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return false;
	}
}
