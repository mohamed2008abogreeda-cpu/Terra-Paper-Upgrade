package com.dfsek.terra.bukkit.nms;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.bukkit.nms.config.VanillaBiomeProperties;


public class NMSBiomeInjector {

    public static <T> Optional<Holder<T>> getEntry(Registry<T> registry, Identifier identifier) {
        return registry.getOptional(identifier)
            .flatMap(registry::getResourceKey)
            .flatMap(registry::get);
    }

    public static Biome createBiome(Biome vanilla, VanillaBiomeProperties vanillaBiomeProperties)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Biome.BiomeBuilder builder = new Biome.BiomeBuilder();

        BiomeSpecialEffects.Builder effects = new BiomeSpecialEffects.Builder();

        effects
            .waterColor(Objects.requireNonNullElse(vanillaBiomeProperties.getWaterColor(), vanilla.getWaterColor()))
            .grassColorModifier(Objects.requireNonNullElse(vanillaBiomeProperties.getGrassColorModifier(),
                vanilla.getSpecialEffects().getGrassColorModifier()));

        if(vanillaBiomeProperties.getGrassColor() == null) {
            vanilla.getSpecialEffects().getGrassColorOverride().ifPresent(effects::grassColorOverride);
        } else {
            effects.grassColorOverride(vanillaBiomeProperties.getGrassColor());
        }

        if(vanillaBiomeProperties.getFoliageColor() == null) {
            vanilla.getSpecialEffects().getFoliageColorOverride().ifPresent(effects::foliageColorOverride);
        } else {
            effects.foliageColorOverride(vanillaBiomeProperties.getFoliageColor());
        }

        builder.hasPrecipitation(Objects.requireNonNullElse(vanillaBiomeProperties.getPrecipitation(), vanilla.hasPrecipitation()));

        builder.temperature(Objects.requireNonNullElse(vanillaBiomeProperties.getTemperature(), vanilla.getBaseTemperature()));

        builder.downfall(Objects.requireNonNullElse(vanillaBiomeProperties.getDownfall(), vanilla.climateSettings.downfall()));

        builder.temperatureAdjustment(
            Objects.requireNonNullElse(vanillaBiomeProperties.getTemperatureModifier(), vanilla.climateSettings.temperatureModifier()));

        builder.mobSpawnSettings(Objects.requireNonNullElse(vanillaBiomeProperties.getSpawnSettings(), vanilla.getMobSettings()));

        // Build environment attributes for colors that moved to EnvironmentAttributeMap in 1.21.11
        EnvironmentAttributeMap.Builder envBuilder = EnvironmentAttributeMap.builder();
        envBuilder.set(EnvironmentAttributes.FOG_COLOR,
            Objects.requireNonNullElse(vanillaBiomeProperties.getFogColor(), vanilla.getSpecialEffects().getFogColor()));
        envBuilder.set(EnvironmentAttributes.WATER_FOG_COLOR,
            Objects.requireNonNullElse(vanillaBiomeProperties.getWaterFogColor(), vanilla.getSpecialEffects().getWaterFogColor()));
        envBuilder.set(EnvironmentAttributes.SKY_COLOR,
            Objects.requireNonNullElse(vanillaBiomeProperties.getSkyColor(), vanilla.getSpecialEffects().getSkyColor()));

        return builder
            .specialEffects(effects.build())
            .putAttributes(envBuilder.build())
            .generationSettings(new BiomeGenerationSettings.PlainBuilder().build())
            .build();
    }

    public static String createBiomeID(ConfigPack pack, com.dfsek.terra.api.registry.key.RegistryKey biomeID) {
        return pack.getID()
                   .toLowerCase() + "/" + biomeID.getNamespace().toLowerCase(Locale.ROOT) + "/" + biomeID.getID().toLowerCase(Locale.ROOT);
    }
}
