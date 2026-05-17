package com.mcupdater.reconstructor.setup;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_DEBUG = "debug";
    public static ModConfigSpec.IntValue ENERGY_PER_POINT;
    public static ModConfigSpec.IntValue STORAGE_MULTIPLIER;
    public static ModConfigSpec.BooleanValue RESTRICT_REPAIRS;
    public static ModConfigSpec.BooleanValue SCALED_REPAIR;
    public static ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
    public static ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST;
    public static ModConfigSpec.IntValue COOLDOWN;

    public static ModConfigSpec.BooleanValue DEBUG;

    public static ModConfigSpec COMMON_CONFIG;

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").translation("reconstructor.config.general").push(CATEGORY_GENERAL);
        ENERGY_PER_POINT = COMMON_BUILDER.comment("How much energy is required per cycle.").translation("reconstructor.config.general.energy_use").defineInRange("EnergyUse",50,0,Integer.MAX_VALUE);
        STORAGE_MULTIPLIER = COMMON_BUILDER.comment("How much energy should be stored by the Reconstructor as a multiple of the repair cost").translation("reconstructor.config.general.storage_multiplier").defineInRange("StorageMultipier",1000, 0, Integer.MAX_VALUE);
        RESTRICT_REPAIRS = COMMON_BUILDER.comment("If true, will only repair things that extend the tool, armor, sword and bow classes.").translation("reconstructor.config.general.restrict").define("Restricted", false);
        SCALED_REPAIR = COMMON_BUILDER.comment("Repair amount per tick will scale based on durability").translation("reconstructor.config.general.scaled").define("Scaled", true);
        BLACKLIST = COMMON_BUILDER.comment("Item classes that appear in this list will not be repaired by the Reconstructor.").translation("reconstructor.config.general.blacklist").defineList("blacklist", new ArrayList<String>(), String::new, (Object o) -> true);
        WHITELIST = COMMON_BUILDER.comment("Package names of mods that should be repaired by the Reconstructor.").translation("reconstructor.config.general.whitelist").defineList("whitelist", Arrays.asList("slimeknights.tconstruct","landmaster.plustic.tools","c4.conarm.common.items.armor","net.silentchaos512.gear.item.gear"), String::new, (Object o) -> true);
        COOLDOWN = COMMON_BUILDER.comment("Number of ticks between repair ticks for the Portable Reconstructor").translation("reconstructor.config.general.cooldown").defineInRange("cooldown",10,0,Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Debug settings").translation("reconstructor.config.debug").push(CATEGORY_DEBUG);
        DEBUG = COMMON_BUILDER.comment("Write class and reason info to log when attempting to repair an item").translation("reconstructor.config.debug.debug").define("debug",false);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
