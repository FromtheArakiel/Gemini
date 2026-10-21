package dev.arakiel.gemini.locate.internal;

import net.minecraft.world.level.biome.Climate;

final class BiomeEnvelope {
    private long temperatureMin = Long.MAX_VALUE;
    private long temperatureMax = Long.MIN_VALUE;
    private long humidityMin = Long.MAX_VALUE;
    private long humidityMax = Long.MIN_VALUE;
    private long continentalnessMin = Long.MAX_VALUE;
    private long continentalnessMax = Long.MIN_VALUE;
    private long erosionMin = Long.MAX_VALUE;
    private long erosionMax = Long.MIN_VALUE;
    private long weirdnessMin = Long.MAX_VALUE;
    private long weirdnessMax = Long.MIN_VALUE;
    private boolean empty = true;

    void add(Climate.ParameterPoint point) {
        temperatureMin = Math.min(temperatureMin, point.temperature().min());
        temperatureMax = Math.max(temperatureMax, point.temperature().max());
        humidityMin = Math.min(humidityMin, point.humidity().min());
        humidityMax = Math.max(humidityMax, point.humidity().max());
        continentalnessMin = Math.min(continentalnessMin, point.continentalness().min());
        continentalnessMax = Math.max(continentalnessMax, point.continentalness().max());
        erosionMin = Math.min(erosionMin, point.erosion().min());
        erosionMax = Math.max(erosionMax, point.erosion().max());
        weirdnessMin = Math.min(weirdnessMin, point.weirdness().min());
        weirdnessMax = Math.max(weirdnessMax, point.weirdness().max());
        empty = false;
    }

    void add(BiomeEnvelope other) {
        if (other.empty) {
            return;
        }
        temperatureMin = Math.min(temperatureMin, other.temperatureMin);
        temperatureMax = Math.max(temperatureMax, other.temperatureMax);
        humidityMin = Math.min(humidityMin, other.humidityMin);
        humidityMax = Math.max(humidityMax, other.humidityMax);
        continentalnessMin = Math.min(continentalnessMin, other.continentalnessMin);
        continentalnessMax = Math.max(continentalnessMax, other.continentalnessMax);
        erosionMin = Math.min(erosionMin, other.erosionMin);
        erosionMax = Math.max(erosionMax, other.erosionMax);
        weirdnessMin = Math.min(weirdnessMin, other.weirdnessMin);
        weirdnessMax = Math.max(weirdnessMax, other.weirdnessMax);
        empty = false;
    }

    boolean empty() {
        return empty;
    }

    boolean matches(long temperature, long humidity, long continentalness, long erosion, long weirdness) {
        return !empty && (within(temperatureMin, temperatureMax, temperature)
                && within(humidityMin, humidityMax, humidity)
                && within(continentalnessMin, continentalnessMax, continentalness)
                && within(erosionMin, erosionMax, erosion)
                && within(weirdnessMin, weirdnessMax, weirdness));
    }

    private static boolean within(long min, long max, long value) {
        return min + 20_000L <= max || value >= min && value <= max;
    }
}
