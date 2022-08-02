package com.mcupdater.reconstructor.setup;

import com.mcupdater.mculib.helpers.DataHelper;
import com.mcupdater.mculib.setup.ModSetup;
import com.mcupdater.reconstructor.block.ReconstructorBlock;
import com.mcupdater.reconstructor.block.ReconstructorEntity;
import com.mcupdater.reconstructor.block.ReconstructorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mcupdater.reconstructor.Reconstructor.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<ReconstructorBlock> RECONSTRUCTOR_BLOCK = BLOCKS.register("reconstructor", ReconstructorBlock::new);
    public static final RegistryObject<Item> RECONSTRUCTOR_BLOCKITEM = ITEMS.register("reconstructor", () -> new BlockItem(RECONSTRUCTOR_BLOCK.get(), new Item.Properties().tab(ModSetup.MCULIB_ITEM_GROUP)));
    public static final RegistryObject<BlockEntityType<ReconstructorEntity>> RECONSTRUCTOR_ENTITY = TILES.register("reconstructor", () -> BlockEntityType.Builder.of(ReconstructorEntity::new, RECONSTRUCTOR_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<ReconstructorMenu>> RECONSTRUCTOR_MENU = CONTAINERS.register("reconstructor", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        ReconstructorEntity te = (ReconstructorEntity) world.getBlockEntity(pos);
        return new ReconstructorMenu(windowId, world, pos, inv, inv.player, te.data, DataHelper.readDirectionMap(data));
    }));
}
