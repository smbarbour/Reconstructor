package com.mcupdater.reconstructor.setup;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_DEBUG = "debug";
    public static ForgeConfigSpec.IntValue ENERGY_PER_POINT;
    public static ForgeConfigSpec.IntValue STORAGE_MULTIPLIER;
    public static ForgeConfigSpec.BooleanValue RESTRICT_REPAIRS;
    public static ForgeConfigSpec.BooleanValue SCALED_REPAIR;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST;

    public static ForgeConfigSpec.BooleanValue DEBUG;

    public static ForgeConfigSpec COMMON_CONFIG;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        ENERGY_PER_POINT = COMMON_BUILDER.comment("How much energy is required per point of damage.").defineInRange("EnergyUse",50,0,Integer.MAX_VALUE);
        STORAGE_MULTIPLIER = COMMON_BUILDER.comment("How much energy should be stored by the Reconstructor as a multiple of the repair cost").defineInRange("StorageMultipier",1000, 0, Integer.MAX_VALUE);
        RESTRICT_REPAIRS = COMMON_BUILDER.comment("If true, will only repair things that extend the tool, armor, sword and bow classes.").define("Restricted", false);
        SCALED_REPAIR = COMMON_BUILDER.comment("Repair amount per tick will scale based on durability").define("Scaled", true);
        BLACKLIST = COMMON_BUILDER.comment("Item classes that appear in this list will not be repaired by the Reconstructor.").defineList("blacklist", new ArrayList<String>(), (Object o) -> true);
        WHITELIST = COMMON_BUILDER.comment("Package names of mods that should be repaired by the Reconstructor.").defineList("whitelist", Arrays.asList("slimeknights.tconstruct.tools","landmaster.plustic.tools","c4.conarm.common.items.armor"), (Object o) -> true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Debug settings").push(CATEGORY_DEBUG);
        DEBUG = COMMON_BUILDER.comment("Write class and reason info to log when attempting to repair an item").define("debug",false);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
