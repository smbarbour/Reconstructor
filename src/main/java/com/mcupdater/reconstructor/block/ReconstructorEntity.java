package com.mcupdater.reconstructor.block;

import com.mcupdater.mculib.block.AbstractMachineBlockEntity;
import com.mcupdater.mculib.capabilities.ItemResourceHandler;
import com.mcupdater.mculib.helpers.DataHelper;
import com.mcupdater.mculib.helpers.DebugHelper;
import com.mcupdater.reconstructor.Reconstructor;
import com.mcupdater.reconstructor.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.mcupdater.reconstructor.setup.Registration.RECONSTRUCTOR_ENTITY;

public class ReconstructorEntity extends AbstractMachineBlockEntity {

    public ReconstructorEntity(BlockPos blockPos, BlockState blockState) {
        super(RECONSTRUCTOR_ENTITY.get(), blockPos, blockState, Config.ENERGY_PER_POINT.get() * Config.STORAGE_MULTIPLIER.get(), Integer.MAX_VALUE, Config.ENERGY_PER_POINT.get(), 1);
        ItemResourceHandler itemResourceHandler = new ItemResourceHandler(this.level, 1, new int[]{0}, new int[]{0}, new int[]{0}, this::stillValid);
        itemResourceHandler.setInsertFunction(this::canPlaceItem);
        itemResourceHandler.setExtractFunction(this::canTakeItem);
        this.configMap.put("items", itemResourceHandler);
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        if (Config.DEBUG.get()) {
            StringBuilder message = new StringBuilder();
            message.append("Item details for ").append(stack.getItem().getDescriptionId()).append("\n");
            message.append("Is Damageable: ").append(stack.isDamageableItem()).append("\n");
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
            Reconstructor.LOGGER.info(message.toString());
        }
        return Reconstructor.canRepair(stack);
        //(stack.isDamageableItem() && stack.isDamaged()) || isWhitelisted(stack.getItem().getClass().toString());
    }

    @Override
    protected boolean performWork() {
        ItemResourceHandler itemStorage = (ItemResourceHandler) this.configMap.get("items");
        if (itemStorage.getItem(0).isEmpty() || !itemStorage.getItem(0).isDamaged())
            return false;
        ItemStack stack = itemStorage.getItem(0);
        int repairAmount = Config.SCALED_REPAIR.get() ? Math.max(1, (stack.getMaxDamage() / 1000)) : 1;
        stack.setDamageValue(stack.getDamageValue() - repairAmount);
        /* TODO - Reimplement tag handling
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Stats")) {
            CompoundTag stats = tag.getCompound("Stats");
            stats.putBoolean("Broken", false);
            tag.put("Stats", stats);
            stack.setTag(tag);
        }
        */
        return true;
    }

    public boolean canTakeItem(int slot, ItemStack stack) {
        if (Config.DEBUG.get()) {
            Reconstructor.LOGGER.debug("Damage check: {}, Repairable/Whitelisted check: {}, Blacklist check: {}, Restricted check: {}",
                    !stack.isDamaged(),
                    !(stack.isRepairable() || isWhitelisted(stack.getItem().getClass().toString())),
                    Config.BLACKLIST.get().contains(stack.getItem().getDescriptionId()),
                    (Config.RESTRICT_REPAIRS.get() && !this.isRestrictedItem(stack.getItem()))
            );
        }
        return
                !stack.isDamaged() ||
                        !(
                                stack.isRepairable() ||
                                        isWhitelisted(stack.getItem().getClass().toString())
                        ) ||
                        Config.BLACKLIST.get().contains(stack.getItem().getDescriptionId()) ||
                        (
                                !Config.RESTRICT_REPAIRS.get() || this.isRestrictedItem(stack.getItem())
                        );
    }

    public static boolean isWhitelisted(String className) {
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

    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new ReconstructorMenu(i, this.level, this.worldPosition, playerInventory, playerEntity, new SimpleContainerData(2), DataHelper.getAdjacentNames(this.level, this.worldPosition));
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.reconstructor.reconstructor");
    }

    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }
}