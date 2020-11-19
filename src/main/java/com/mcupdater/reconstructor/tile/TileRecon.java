package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.capabilities.TileEntityPowered;
import com.mcupdater.mculib.helpers.DebugHelper;
import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.setup.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static com.mcupdater.reconstructor.setup.Registration.RECONBLOCK_TILE;

public class TileRecon extends TileEntityPowered implements ISidedInventory {
    protected NonNullList<ItemStack> itemStorage = NonNullList.withSize(1, ItemStack.EMPTY);

    private final LazyOptional<IItemHandlerModifiable>[] itemHandler = SidedInvWrapper.create(this, Direction.values());

    public TileRecon() {
        super(RECONBLOCK_TILE.get(), Config.ENERGY_PER_POINT.get() * Config.STORAGE_MULTIPLIER.get(), Integer.MAX_VALUE);
    }

    public int getSizeInventory() {
        return this.itemStorage.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.itemStorage) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.itemStorage.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.itemStorage, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.itemStorage, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.itemStorage.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (Config.DEBUG.get()) {
            StringBuilder message = new StringBuilder();
            message.append("Item details for ").append(stack.getItem().getTranslationKey()).append("\n");
            message.append("Is Damaged: ").append(stack.isDamaged()).append("\n");
            message.append("Is Repairable: ").append(stack.isRepairable()).append("\n");
            message.append("Is Whitelisted: ").append(isWhitelisted(stack.getItem().getClass().toString())).append("\n");
            message.append("Is Blacklisted: ").append(Config.BLACKLIST.get().contains(stack.getItem().getTranslationKey())).append("\n");
            message.append("Is Restricted: ").append((Config.RESTRICT_REPAIRS.get() && !(stack.getItem() instanceof ToolItem || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof SwordItem || stack.getItem() instanceof BowItem))).append("\n");
            message.append("Class hierarchy: ").append(stack.getItem().getClass().toString()).append("\n");
            Set<Class<?>> classes = DebugHelper.getAllExtendedOrImplementedTypesRecursively(stack.getItem().getClass());
            for (Class<?> clazz : classes) {
                message.append("  ").append(clazz.getName()).append("\n");
            }
            Reconstructor.LOGGER.log(Level.INFO, message.toString());
        }
        return stack.isDamageable() || isWhitelisted(stack.getItem().getClass().toString());
    }


    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void read(BlockState blockState, CompoundNBT compound) {
        super.read(blockState, compound);
        this.itemStorage = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.itemStorage);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ItemStackHelper.saveAllItems(compound, this.itemStorage);
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (Config.ENERGY_PER_POINT.get() == 0 || energyStorage.getEnergyStored() > Config.ENERGY_PER_POINT.get()) {
                if (tryRepair()) {
                    energyStorage.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
                    this.markDirty();
                }
            }
        }
        super.func_73660_a();
        if (Config.DEBUG.get()) {
            energyStorage.receiveEnergy(1,false);
            this.markDirty();
        }
    }

    private boolean tryRepair() {
        if (this.getStackInSlot(0).isEmpty() || !this.getStackInSlot(0).isDamaged())
            return false;
        ItemStack stack = this.getStackInSlot(0);
        int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (stack.getMaxDamage()/1000)) : 1;
        stack.setDamage(stack.getDamage() - repairAmount);
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("Stats")) {
            CompoundNBT stats = tag.getCompound("Stats");
            stats.putBoolean("Broken",false);
            tag.put("Stats", stats);
            stack.setTag(tag);
        }
        return true;
    }

    public boolean isExtractable(ItemStack stack) {
        return
                !stack.isDamaged() ||
                !(
                        stack.isRepairable() ||
                        isWhitelisted(stack.getItem().getClass().toString())
                ) ||
                Config.BLACKLIST.get().contains(stack.getItem().getTranslationKey()) ||
                (
                        Config.RESTRICT_REPAIRS.get() &&
                        !(
                                stack.getItem() instanceof ToolItem ||
                                stack.getItem() instanceof ArmorItem ||
                                stack.getItem() instanceof SwordItem ||
                                stack.getItem() instanceof BowItem
                        )
                );
    }

    private boolean isWhitelisted(String className) {
        for (String entry : Config.WHITELIST.get()) {
            if (className.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return itemHandler[side != null ? side.ordinal() : 0].cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void clear() {

    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        if (stack.isItemEqual(getStackInSlot(index))) {
            return isExtractable(getStackInSlot(index));
        } else {
            return false;
        }
    }
}
