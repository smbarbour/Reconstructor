package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.capabilities.TileEntityPowered;
import com.mcupdater.mculib.helpers.DebugHelper;
import com.mcupdater.mculib.helpers.InventoryHelper;
import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

public class TileRecon extends TileEntityPowered implements WorldlyContainer, MenuProvider {
    protected NonNullList<ItemStack> itemStorage = NonNullList.withSize(1, ItemStack.EMPTY);
    private boolean autoEject = false;

    private final LazyOptional<IItemHandlerModifiable>[] itemHandler = SidedInvWrapper.create(this, Direction.values());

    public DataSlot data = new DataSlot() {
        @Override
        public int get() {
            return isAutoEject() ? 1 : 0;
        }

        @Override
        public void set(int newValue) {
            setAutoEject(newValue != 0);
        }
    };

    public TileRecon(BlockPos blockPos, BlockState blockState ) {
        super(RECONBLOCK_TILE.get(), blockPos, blockState,Config.ENERGY_PER_POINT.get() * Config.STORAGE_MULTIPLIER.get(), Integer.MAX_VALUE);
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int index) {
        return this.itemStorage.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.itemStorage, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.itemStorage, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.itemStorage.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (Config.DEBUG.get()) {
            StringBuilder message = new StringBuilder();
            message.append("Item details for ").append(stack.getItem().getDescriptionId()).append("\n");
            message.append("Is Damaged: ").append(stack.isDamaged()).append("\n");
            message.append("Is Repairable: ").append(stack.isRepairable()).append("\n");
            message.append("Is Whitelisted: ").append(isWhitelisted(stack.getItem().getClass().toString())).append("\n");
            message.append("Is Blacklisted: ").append(Config.BLACKLIST.get().contains(stack.getItem().getDescriptionId())).append("\n");
            message.append("Is Restricted: ").append((Config.RESTRICT_REPAIRS.get() && !isRestrictedItem(stack.getItem()))).append("\n");
            message.append("Class hierarchy: ").append(stack.getItem().getClass().toString()).append("\n");
            Set<Class<?>> classes = DebugHelper.getAllExtendedOrImplementedTypesRecursively(stack.getItem().getClass());
            for (Class<?> clazz : classes) {
                message.append("  ").append(clazz.getName()).append("\n");
            }
            Reconstructor.LOGGER.log(Level.INFO, message.toString());
        }
        return stack.isDamageableItem() || isWhitelisted(stack.getItem().getClass().toString());
    }


    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.itemStorage = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.setAutoEject(compound.getBoolean("autoEject"));
        ContainerHelper.loadAllItems(compound, this.itemStorage);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        ContainerHelper.saveAllItems(compound, this.itemStorage);
        compound.putBoolean("autoEject", this.isAutoEject());
        super.saveAdditional(compound);
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            if (Config.ENERGY_PER_POINT.get() == 0 || energyStorage.getEnergyStored() > Config.ENERGY_PER_POINT.get()) {
                if (tryRepair()) {
                    energyStorage.extractEnergy(Config.ENERGY_PER_POINT.get(), false);
                    this.setChanged();
                }
            }
            if (autoEject && isExtractable(this.getItem(0))) {
                ejectItem();
            }
        }
        super.tick();

    }

    private void ejectItem() {
        if (InventoryHelper.addToPriorityInventory(this.getLevel(), this.worldPosition, getItem(0).copy(), InventoryHelper.getSideList(this.worldPosition, this.getBlockState().getValue(BlockRecon.FACING)))) {
            this.removeItem(0, 1);
        }
    }

    private boolean tryRepair() {
        if (this.getItem(0).isEmpty() || !this.getItem(0).isDamaged())
            return false;
        ItemStack stack = this.getItem(0);
        int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (stack.getMaxDamage()/1000)) : 1;
        stack.setDamageValue(stack.getDamageValue() - repairAmount);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Stats")) {
            CompoundTag stats = tag.getCompound("Stats");
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
                Config.BLACKLIST.get().contains(stack.getItem().getDescriptionId()) ||
                (
                        Config.RESTRICT_REPAIRS.get() && !this.isRestrictedItem(stack.getItem())
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

    private boolean isRestrictedItem(Item item) {
        return
                (item instanceof DiggerItem ||
                item instanceof ShearsItem ||
                item instanceof FishingRodItem ||
                item instanceof ArmorItem ||
                item instanceof ElytraItem ||
                item instanceof SwordItem ||
                item instanceof ShieldItem ||
                item instanceof BowItem);
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
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (stack.sameItem(getItem(index))) {
            return isExtractable(getItem(index));
        } else {
            return false;
        }
    }

    @Override
    public void clearContent() {

    }

    public boolean isAutoEject() {
        return autoEject;
    }

    public void setAutoEject(boolean newValue) {
        this.autoEject = newValue;
        this.setChanged();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerRecon(i, this.level, this.worldPosition, playerInventory, playerEntity, this.data);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("block.reconstructor.reconstructor");
    }
}
