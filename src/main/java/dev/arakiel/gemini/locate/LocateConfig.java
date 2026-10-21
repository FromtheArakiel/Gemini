package dev.arakiel.gemini.locate;

import dev.arakiel.gemini.config.Config;
import dev.arakiel.gemini.config.ConfigEntry;
import dev.arakiel.gemini.config.Range;
import dev.arakiel.gemini.config.RestartRequired;

@Config(value = "locate", nested = true)
public final class LocateConfig {
    @ConfigEntry(value = "scheduler.maxConcurrent", comment = "Maximum locate searches running concurrently.")
    @Range(min = 1, max = 256)
    public int maxConcurrent = 2;

    @ConfigEntry(value = "scheduler.maxQueued", comment = "Maximum locate searches waiting for capacity.")
    @Range(min = 0, max = 10000)
    public int maxQueued = 128;

    @ConfigEntry(value = "features.structureCommand")
    public boolean structureCommand = true;

    @ConfigEntry(value = "features.biomeCommand")
    public boolean biomeCommand = true;

    @ConfigEntry(value = "features.eyeOfEnder")
    public boolean eyeOfEnder = true;

    @ConfigEntry(value = "features.explorationMaps")
    public boolean explorationMaps = true;

    @ConfigEntry(value = "features.villagerMaps")
    public boolean villagerMaps = true;

    @ConfigEntry(value = "features.dolphinTreasure")
    public boolean dolphinTreasure = true;

    @ConfigEntry(value = "features.biomeFilter")
    public boolean biomeFilter = true;

    @ConfigEntry(value = "features.structureBiomeFilter")
    public boolean structureBiomeFilter = true;

    @ConfigEntry(value = "search.biomeRadius")
    @Range(min = 1600, max = 12800)
    public int biomeRadius = 6400;

    @ConfigEntry(value = "search.biomeHorizontalStep")
    @Range(min = 1, max = 512)
    public int biomeHorizontalStep = 32;

    @ConfigEntry(value = "search.biomeVerticalStep")
    @Range(min = 1, max = 512)
    public int biomeVerticalStep = 64;

    @ConfigEntry(value = "search.eyeTimeoutSeconds")
    @Range(min = 1, max = 600)
    public int eyeTimeoutSeconds = 60;

    @ConfigEntry(value = "search.mapTimeoutSeconds")
    @Range(min = 1, max = 600)
    public int mapTimeoutSeconds = 60;

    @ConfigEntry(value = "search.removeInvalidMerchantOffers")
    public boolean removeInvalidMerchantOffers = false;

    @ConfigEntry(value = "scheduler.threads")
    @RestartRequired
    public boolean virtualThreads = true;
}
