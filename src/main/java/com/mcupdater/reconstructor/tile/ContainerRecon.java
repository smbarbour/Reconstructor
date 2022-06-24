package com.mcupdater.reconstructor.tile;

import com.mcupdater.mculib.capabilities.ContainerPowered;
import com.mcupdater.mculib.inventory.ArmorSlotItemHandler;
import com.mcupdater.mculib.inventory.PlayerBypassItemHandler;
import com.mcupdater.mculib.inventory.PlayerPrioritySlotItemHandler;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ContainerRecon extends ContainerPowered {
    private TileRecon localTileEntity;
    private Player playerEntity;
    private IItemHandler playerInventory;

    public final DataSlot data;

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};

    public ContainerRecon(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player, DataSlot localData) {
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
            final EquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new ArmorSlotItemHandler(playerInventory, 36 + (3 - i), 8, 8 + i * 18, entityequipmentslot, playerEntity));
        }
        this.addSlot(new SlotItemHandler(playerInventory, 40, 26, 62).setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(localTileEntity.getLevel(), localTileEntity.getBlockPos()), playerEntity, Registration.RECONBLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerEntity, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, this.getItems().size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (this.localTileEntity.canPlaceItem(0, stack)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                if (index < 29) {
                    if (!this.moveItemStackTo(stack, 29, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.moveItemStackTo(stack, 1, 29, false)) {
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