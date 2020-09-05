package org.mcupdater.reconstructor.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;
import org.mcupdater.reconstructor.Config;
import org.mcupdater.reconstructor.Reconstructor;
import org.mcupdater.reconstructor.gui.ContainerRecon;
import org.mcupdater.reconstructor.helpers.DebugHelper;
import org.mcupdater.reconstructor.helpers.InventoryHelper;
import org.mcupdater.reconstructor.helpers.ReconInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TileRecon extends TileEntityLockableLoot implements ITickable
{
	//private final BasicInventory inv;
	private NonNullList<ItemStack> workspace;
	private EnumFacing orientation = EnumFacing.DOWN;
	private EnergyStorage storage = new EnergyStorage(Config.energyPerPoint * 1000);
	private IItemHandler itemHandler;

	public TileRecon() {
		this.workspace = NonNullList.withSize(1, ItemStack.EMPTY);
		this.itemHandler = new ReconInvWrapper(this);
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			if (Config.energyPerPoint == 0 || storage.getEnergyStored() > Config.energyPerPoint) {
				if (tryRepair()) {
					storage.extractEnergy(Config.energyPerPoint, false);
				}
			}
		}
	}

	public boolean tryRepair() {
		if (getStackInSlot(0).isEmpty())
			return false;

		if (isExtractable()) {
			ejectItem();
			return false;
		}
		int repairAmount = Config.scaledRepair ? Math.max(1, (getStackInSlot(0).getMaxDamage() - getStackInSlot(0).getItemDamage())/100) : 1;
		getStackInSlot(0).setItemDamage(getStackInSlot(0).getItemDamage() - repairAmount);
		NBTTagCompound tag = getStackInSlot(0).getTagCompound();
		if (tag != null && tag.hasKey("Stats")) {
			NBTTagCompound stats = tag.getCompoundTag("Stats");
			stats.setBoolean("Broken",false);
			tag.setTag("Stats", stats);
			getStackInSlot(0).setTagCompound(tag);
		}
		return true;
	}

	public boolean isExtractable() {
		return !getStackInSlot(0).isItemDamaged() || !(getStackInSlot(0).getItem().isRepairable() || isWhitelisted(getStackInSlot(0).getItem().getClass().toString())) || Config.blacklist.contains(getStackInSlot(0).getItem().getUnlocalizedName()) || (Config.restrictRepairs && !(getStackInSlot(0).getItem() instanceof ItemTool || getStackInSlot(0).getItem() instanceof ItemArmor || getStackInSlot(0).getItem() instanceof ItemSword || getStackInSlot(0).getItem() instanceof ItemBow));
	}

	private boolean isWhitelisted(String className) {
		for (String entry : Config.whitelist) {
			if (className.contains(entry)) {
				return true;
			}
		}
		return false;
	}

	private void ejectItem() {
		if (Config.debug) {
			ItemStack stack = getStackInSlot(0);
			Reconstructor.instance.getLogger().log(Level.INFO, "Is Damaged: " + stack.isItemDamaged());
			Reconstructor.instance.getLogger().log(Level.INFO, "Is Repairable: " + stack.getItem().isRepairable());
			Reconstructor.instance.getLogger().log(Level.INFO, "Is Whitelisted: " + isWhitelisted(stack.getItem().getClass().toString()));
			Reconstructor.instance.getLogger().log(Level.INFO, "Is Blacklisted: " + Config.blacklist.contains(stack.getItem().getUnlocalizedName()));
			Reconstructor.instance.getLogger().log(Level.INFO, "Is Restricted: " + (Config.restrictRepairs && !(stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow)));
			Reconstructor.instance.getLogger().log(Level.INFO, "Class hierarchy: " + stack.getItem().getClass().toString());
			Set<Class<?>> classes = DebugHelper.getAllExtendedOrImplementedTypesRecursively(stack.getItem().getClass());
			for (Class<?> clazz : classes) {
				Reconstructor.instance.getLogger().log(Level.INFO, "  " + clazz.getName());
			}
		}
		if (InventoryHelper.addToPriorityInventory(this.getWorld(), this.pos, getStackInSlot(0).copy())) {
			decrStackSize(0, 1);
			return;
		}
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world == null)
		{
			return true;
		}

		if (this.world.getTileEntity(this.pos) != this)
		{
			return false;
		}

		return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64D;
	}


	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public int getSizeInventory() {
		return this.workspace.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.workspace.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		this.fillWithLoot(null);
		if (isExtractable()) {
			ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);

			if (!itemstack.isEmpty()) {
				this.markDirty();
			}

			return itemstack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		this.fillWithLoot(null);
		return isExtractable() ? ItemStackHelper.getAndRemove(this.getItems(), index) : ItemStack.EMPTY;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem().isDamageable();
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.workspace = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(data, this.workspace);
		if (data.hasKey("energy")) {
			storage.receiveEnergy(data.getInteger("energy"),false);
		}
		this.orientation = EnumFacing.VALUES[data.getByte("orientation")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		ItemStackHelper.saveAllItems(data, this.workspace);
		data.setInteger("energy", storage.getEnergyStored());
		data.setByte("orientation", (byte) this.orientation.ordinal());
		return data;
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

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound compound = new NBTTagCompound();

		compound.setByte("orientation", (byte) this.orientation.ordinal());

		return new SPacketUpdateTileEntity(this.pos, 0, compound);
	}

/*	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		if (pkt.getTileEntityType() == 0)
		{
			NBTTagCompound compound = pkt.getNbtCompound();

			this.orientation = EnumFacing.VALUES[compound.getByte("orientation")];
		}
	}*/

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(storage);
		}
		if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) (itemHandler == null ? (itemHandler = new ReconInvWrapper(this)) : itemHandler);

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public NonNullList<ItemStack> getItems()
	{
		return this.workspace;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.workspace)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerRecon(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return "reconstructor:reconstructorblock";
	}

}
