package com.mcupdater.reconstructor.setup;

import com.mcupdater.reconstructor.tile.BlockRecon;
import com.mcupdater.reconstructor.tile.ContainerRecon;
import com.mcupdater.reconstructor.tile.TileRecon;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.mcupdater.reconstructor.Reconstructor.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<BlockRecon> RECONBLOCK = BLOCKS.register("reconstructor", BlockRecon::new);
    public static final RegistryObject<Item> RECONBLOCK_ITEM = ITEMS.register("reconstructor", () -> new BlockItem(RECONBLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<TileEntityType<TileRecon>> RECONBLOCK_TILE = TILES.register("reconstructor", () -> TileEntityType.Builder.of(TileRecon::new, RECONBLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<ContainerRecon>> RECONBLOCK_CONTAINER = CONTAINERS.register("reconstructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileRecon te = (TileRecon) world.getBlockEntity(pos);
        return new ContainerRecon(windowId, world, pos, inv, inv.player, te.data);
    }));
}
