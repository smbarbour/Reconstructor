package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.capabilities.ContainerPowered;
import com.mcupdater.mculib.inventory.ArmorSlotItemHandler;
import com.mcupdater.mculib.inventory.PlayerBypassItemHandler;
import com.mcupdater.mculib.inventory.PlayerPrioritySlotItemHandler;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ContainerRecon extends ContainerPowered {
    private TileRecon localTileEntity;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;

    public final IntReferenceHolder data;

    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET, EquipmentSlotType.OFFHAND};

    public ContainerRecon(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, IntReferenceHolder localData) {
        super(Registration.RECONBLOCK_CONTAINER.get(), windowId);
        localTileEntity = world.getBlockEntity(pos) instanceof TileRecon ? (TileRecon) world.getBlockEntity(pos) : null;
        tileEntity = localTileEntity;
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.data = localData;

        if (localTileEntity != null) {
            localTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new PlayerPrioritySlotItemHandler(new PlayerBypassItemHandler(h, localTileEntity), 0, 80, 41));
            });
        }
        layoutPlayerInventorySlots(8,84);
        trackPower();
        addDataSlot(this.data);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);

        for (int i = 0; i < 4; ++i)
        {
            final EquipmentSlotType entityequipmentslot = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new ArmorSlotItemHandler(playerInventory, 36 + (3 - i), 8, 8 + i * 18, entityequipmentslot, playerEntity));
        }
        this.addSlot(new SlotItemHandler(playerInventory, 40, 26, 62).setBackground(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(IWorldPosCallable.create(localTileEntity.getLevel(), localTileEntity.getBlockPos()), playerEntity, Registration.RECONBLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerEntity, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (this.localTileEntity.isExtractable(stack)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                if (index < 28) {
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, stack);
        }
        return itemstack;
    }

    public TileRecon getBlockEntity() {
        return localTileEntity;
    }
}
