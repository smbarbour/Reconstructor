package org.mcupdater.reconstructor;

import cofh.api.energy.TileEnergyHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class TileRecon extends TileEnergyHandler implements ITickable, ISidedInventory
{
	private final BasicInventory inv;
	private EnumFacing orientation = EnumFacing.DOWN;

	public TileRecon(){
		inv = new BasicInventory(1,"Processing",1);
	}

	@Override
	public void update() {
		if (storage.getEnergyStored() > Reconstructor.energyPerPoint) {
			if (tryRepair()) {
				storage.extractEnergy(Reconstructor.energyPerPoint, false);
			}
		}
	}

	public boolean tryRepair() {
		if (getStackInSlot(0) == null)
			return false;

		if (!getStackInSlot(0).isItemDamaged() || !(getStackInSlot(0).getItem().isRepairable() || getStackInSlot(0).getItem().getClass().toString().contains("slimeknights.tconstruct.tools")) || Reconstructor.blacklist.contains(getStackInSlot(0).getItem().getUnlocalizedName()) || (Reconstructor.instance.restrictRepairs && !(getStackInSlot(0).getItem() instanceof ItemTool || getStackInSlot(0).getItem() instanceof ItemArmor || getStackInSlot(0).getItem() instanceof ItemSword || getStackInSlot(0).getItem() instanceof ItemBow))) {
			ejectItem();
			return false;
		}
		getStackInSlot(0).setItemDamage(getStackInSlot(0).getItemDamage() - 1);
		if (getStackInSlot(0).getItem().getClass().toString().contains("slimeknights.tconstruct.tools")) {
			NBTTagCompound tag = getStackInSlot(0).getTagCompound();
			if (tag != null && tag.hasKey("Stats")) {
				NBTTagCompound stats = tag.getCompoundTag("Stats");
				stats.setBoolean("Broken",false);
				tag.setTag("Stats", stats);
				getStackInSlot(0).setTagCompound(tag);
			}
		}
		return true;
	}

	private void ejectItem() {
		if (Utils.addToPriorityInventory(worldObj, this.pos, getStackInSlot(0))) {
			decrStackSize(0, 1);
			return;
		}
		
		float f = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityitem = new EntityItem(this.worldObj, this.pos.getX() + f, this.pos.getY() + f1 + 0.5F, this.pos.getZ() + f2, getStackInSlot(0));

		float f3 = 0.05F;
		entityitem.motionX = (float) this.worldObj.rand.nextGaussian() * f3;
		entityitem.motionY = (float) this.worldObj.rand.nextGaussian() * f3 + 1.0F;
		entityitem.motionZ = (float) this.worldObj.rand.nextGaussian() * f3;
		this.worldObj.spawnEntityInWorld(entityitem);
		
		decrStackSize(0, 1);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

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

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(slot, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem().isDamageable();
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		inv.readFromNBT(data);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		inv.writeToNBT(data);
		return data;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public String getName() {
		return "Reconstructor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	public EnumFacing getOrientation() {
		return orientation;
	}

	public void setOrientation(EnumFacing orientation) {
		this.orientation = orientation;
	}
}
