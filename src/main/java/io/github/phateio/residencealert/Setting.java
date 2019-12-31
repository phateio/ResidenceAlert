package io.github.phateio.residencealert;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.*;

public class Setting {

    private static final String path_include_server_land = "include-server-land";
    static boolean includeServerLand;

    private static final String path_disabled_worlds = "disabled-worlds";
    static Set<String> disabledWorlds;

    private static final String path_ignore_y_below = "ignore-y-below";
    static int ignoreYBelow;

    private static final String path_enabled_claims = "enabled-residences";
    static Set<String> enabledClaims;

    private static final String path_residence_owner_list = "alert-residence-owner";
    static Set<String> alertResidenceOwner;

    private static final String path_message = "alert-message";
    static String alertMessage;

    private static final String path_blocks = "alert-blocks-type-display";
    static Set<Material> alertBlocks;

    private static final String path_ignoreNearbyBlocks = "ignore-nearby-blocks";
    static Set<Material> ignoreNearbyBlocks;

    static final List<BlockFace> checkFaces = Arrays.asList(DOWN, UP, EAST, WEST, SOUTH, NORTH);

    static void syncFile(Logger logger, FileConfiguration config) {
        writeDefault(config);
        config.options().copyDefaults(true);
        readConfig(config, logger);
    }

    private static void writeDefault(FileConfiguration config) {
        includeServerLand = false;
        config.addDefault(path_include_server_land, false);

        disabledWorlds = Sets.newHashSet("world2014_nether");
        config.addDefault(path_disabled_worlds, new ArrayList<>(disabledWorlds));

        ignoreYBelow = 64;
        config.addDefault(path_ignore_y_below, ignoreYBelow);

        enabledClaims = Sets.newHashSet("Spawn2014.village");
        config.addDefault(path_enabled_claims, new ArrayList<>(enabledClaims));

        alertResidenceOwner = Collections.emptySet();
        config.addDefault(path_residence_owner_list, new ArrayList<>(alertResidenceOwner));

        alertMessage = "&c[Honeypot] player ${Player} placed ${Block} on ${onClicked} at {world=${world}, x=${x}, y=${y}, z=${z}}";
        config.addDefault(path_message, alertMessage);

        alertBlocks = Sets.newHashSet(LAVA_BUCKET, FIRE);
        config.addDefault(path_blocks, toStringList(alertBlocks));

        ignoreNearbyBlocks = Sets.newHashSet(GRASS_BLOCK, WATER, OBSIDIAN);
        config.addDefault(path_ignoreNearbyBlocks, toStringList(ignoreNearbyBlocks));
    }

    private static List<String> toStringList(Collection<Material> collection) {
        return collection.stream().map(Material::name).collect(Collectors.toList());
    }

    private static void readConfig(FileConfiguration config, Logger logger) {
        includeServerLand = config.getBoolean(path_include_server_land, includeServerLand);
        disabledWorlds = new HashSet<>(config.getStringList(path_disabled_worlds));
        ignoreYBelow = config.getInt(path_ignore_y_below, ignoreYBelow);
        enabledClaims = new HashSet<>(config.getStringList(path_enabled_claims));
        alertResidenceOwner = new HashSet<>(config.getStringList(path_include_server_land));
        alertMessage = config.getString(path_message, alertMessage);
        alertBlocks = toMaterialSet(config.getStringList(path_blocks), logger);
        ignoreNearbyBlocks = toMaterialSet(config.getStringList(path_ignoreNearbyBlocks), logger);
    }

    private static Set<Material> toMaterialSet(List<String> list, Logger logger) {
        return list.stream().map(name -> {
            try {
                return Material.valueOf(name);
            } catch (IllegalArgumentException e) {
                logger.warning(name + " is not a Material value.");
                return null;
            }
        }).filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
    }

}
