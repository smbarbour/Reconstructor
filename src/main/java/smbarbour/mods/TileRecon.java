package smbarbour.mods;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import smbarbour.mods.shared.BCInteract;
import smbarbour.mods.shared.BasicInventory;
import smbarbour.mods.shared.Utils;

public class TileRecon extends TileEntity implements IPowerReceptor, IInventory {
	public PowerHandler powerHandler;
	private final BasicInventory inv;
	public static final int MAX_ENERGY = 1500;

	public TileRecon(){
		powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
		initPowerProvider();
		
		inv = new BasicInventory(1,"Processing",1);
	}

	private void initPowerProvider() {
		powerHandler.configure((Reconstructor.energyPerPoint * 2), (Reconstructor.energyPerPoint * 20), Reconstructor.energyPerPoint, MAX_ENERGY);
		powerHandler.configurePowerPerdition(1, 1);
	}
	
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerHandler.getPowerReceiver();
	}

	@Override
	public void updateEntity() {
		this.getPowerReceiver(null).update();
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if (getStackInSlot(0) == null)
			return;
			
		if (!getStackInSlot(0).isItemDamaged() || !getStackInSlot(0).getItem().isRepairable()) {
			ejectItem();
			return;
		}
		float powerUsed = powerHandler.useEnergy(Reconstructor.energyPerPoint, (Reconstructor.energyPerPoint * 10), true);
		if (powerUsed < (float) Reconstructor.energyPerPoint){
			return;		
		}
		int iterations = (int)(powerUsed / Reconstructor.energyPerPoint);
		int iteration = 0;
		while (getStackInSlot(0).isItemDamaged() && iteration < iterations) {
			getStackInSlot(0).damageItem(-1, new GremlinEntity(this.worldObj));
			iteration++;
		}
	}

	@Override
	public World getWorld() {
		return null;
	}

	private void ejectItem() {
		if (Reconstructor.instance.doPipeInteract) {
			if (BCInteract.addToPipe(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN, getStackInSlot(0)) > 0) {
				decrStackSize(0, 1);
				return;
			}
		}

		if (Utils.addToRandomInventory(worldObj, xCoord, yCoord, zCoord, getStackInSlot(0)) > 0) {
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
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
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
	public String getInvName() {
		return "Reconstructor";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
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
}
