package com.mcupdater.reconstructor;

import com.mcupdater.reconstructor.setup.Config;
import com.mcupdater.reconstructor.setup.ModSetup;
import com.mcupdater.reconstructor.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reconstructor.MODID)
public class Reconstructor
{
    public static final String MODID = "reconstructor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Reconstructor(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Registration.init(modEventBus);

        modEventBus.addListener(ModSetup::init);
    }

    public static boolean canRepair(ItemStack stack) {
        return !isBlacklisted(stack) &&
                (stack.isDamageableItem() && stack.isDamaged()) &&
                (stack.isRepairable() || isWhitelisted(stack)) &&
                (!Config.RESTRICT_REPAIRS.get() || isRestrictedItem(stack.getItem()));
    }

    private static boolean isBlacklisted(ItemStack stack) {
        return Config.BLACKLIST.get().contains(stack.getItem().getClass().toString());
    }

    private static boolean isWhitelisted(ItemStack stack) {
        String className = stack.getItem().getClass().toString();
        for (String entry : Config.WHITELIST.get()) {
            if (className.contains(entry))
                return true;
        }
        return false;
    }

    private static boolean isRestrictedItem(Item item) {
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
}
