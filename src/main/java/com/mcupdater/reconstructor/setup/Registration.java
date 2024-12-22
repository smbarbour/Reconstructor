package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.block.ReconstructorBlock;
import com.mcupdater.reconstructor.block.ReconstructorEntity;
import com.mcupdater.reconstructor.block.ReconstructorMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mcupdater.reconstructor.Reconstructor.MODID;

public class Registration {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
    }

    public static final DeferredBlock<ReconstructorBlock> RECONSTRUCTOR_BLOCK = BLOCKS.register(
            "reconstructor",
            () -> new ReconstructorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(10.0f,200.0f)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final DeferredItem<Item> RECONSTRUCTOR_BLOCKITEM = ITEMS.register(
            "reconstructor",
            () -> new BlockItem(RECONSTRUCTOR_BLOCK.get(), new Item.Properties())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReconstructorEntity>> RECONSTRUCTOR_ENTITY = BLOCK_ENTITIES.register(
            "reconstructor",
            () -> BlockEntityType.Builder.of(ReconstructorEntity::new, RECONSTRUCTOR_BLOCK.get()).build(null)
    );
    public static final Supplier<MenuType<ReconstructorMenu>> RECONSTRUCTOR_MENU = MENUS.register(
            "reconstructor",
            () -> IMenuTypeExtension.create(ReconstructorMenu::factory)
    );
}
