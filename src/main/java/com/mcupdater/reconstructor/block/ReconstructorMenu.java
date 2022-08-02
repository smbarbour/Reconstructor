package com.mcupdater.reconstructor.block;

import com.mcupdater.mculib.block.AbstractMachineMenu;
import com.mcupdater.mculib.inventory.ArmorSlotItemHandler;
import com.mcupdater.mculib.inventory.MachineInputSlot;
import com.mcupdater.reconstructor.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Map;

public class ReconstructorMenu extends AbstractMachineMenu<ReconstructorEntity> {

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};

    public ReconstructorMenu(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player, ContainerData data, Map<Direction, Component> directionComponentMap) {
        super((ReconstructorEntity) world.getBlockEntity(pos),Registration.RECONSTRUCTOR_MENU.get(), windowId, world, pos, playerInventory, player, data, directionComponentMap);
    }

    @Override
    protected void addMachineSlots() {
        addSlot(new MachineInputSlot(this.machineEntity, new InvWrapper(this.machineEntity), 0, 80, 41));
    }

    @Override
    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        super.layoutPlayerInventorySlots(leftCol, topRow);

        for (int i = 0; i < 4; ++i)
        {
            final EquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new ArmorSlotItemHandler(playerInventory, 36 + (3 - i), 8, 8 + i * 18, entityequipmentslot, this.player));
        }
        this.addSlot(new SlotItemHandler(playerInventory, 40, 26, 62).setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(this.machineEntity.getLevel(), this.machineEntity.getBlockPos()), playerIn, Registration.RECONSTRUCTOR_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerEntity, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                boolean itemNotMoved = true;
                if (stack.getItem() instanceof ShieldItem) { // If shield, try to place in offhand
                    itemNotMoved = !this.moveItemStackTo(stack,41,this.getItems().size(),false);
                }
                // Place in inventory starting with armor slots first, then hotbar, then regular inventory (true means reverse order)
                if (itemNotMoved && !this.moveItemStackTo(stack, 1, this.getItems().size()-1, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                // Move to repair slot if possible
                if (this.machineEntity.itemStorage.get(0).isEmpty() && this.machineEntity.canPlaceItem(0, stack)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    // Move regular inventory to hotbar
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                    // Move hotbar to regular inventory
                } else if (index < 38 && !this.moveItemStackTo(stack, 1, 29, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(stack,1, 37,false)) { //Move armor to regular inventory or hotbar
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

}