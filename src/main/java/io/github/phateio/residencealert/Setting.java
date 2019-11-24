package io.github.phateio.residencealert;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.bukkit.Material.FIRE;
import static org.bukkit.Material.LAVA_BUCKET;

public class Setting {

    private static final String path_include_server_owner = "include-server-owner";
    static boolean includeServerOwner;

    private static final String path_residence_owner_list = "alert-residence-owner";
    static Set<String> alertResidenceOwner;

    private static final String path_message = "alert-message";
    static String alertMessage;

    private static final String path_blocks = "alert-blocks-type-display";
    static Set<Material> alertBlocks;

    static void syncFile(Logger logger, FileConfiguration config) {
        writeDefault(config);
        config.options().copyDefaults(true);
        readConfig(config, logger);
    }

    private static void writeDefault(FileConfiguration config) {
        includeServerOwner = true;
        config.addDefault(path_include_server_owner, true);

        alertResidenceOwner = Collections.emptySet();
        config.addDefault(path_residence_owner_list, new ArrayList<>(alertResidenceOwner));

        alertMessage = "&c[Honeypot] player ${Player} placed ${Block} block at {world=${world}, x=${x}, y=${y}, z=${z}}";
        config.addDefault(path_message, alertMessage);

        alertBlocks = new HashSet<>(Arrays.asList(LAVA_BUCKET, FIRE));
        final List<String> blocksNameList = alertBlocks.stream().map(Material::name).collect(Collectors.toList());
        config.addDefault(path_blocks, blocksNameList);
    }

    private static void readConfig(FileConfiguration config, Logger logger) {
        includeServerOwner = config.getBoolean(path_include_server_owner, includeServerOwner);
        alertResidenceOwner = new HashSet<>(config.getStringList(path_include_server_owner));
        alertMessage = config.getString(path_message, alertMessage);
        alertBlocks = config.getStringList(path_blocks).stream().map(name -> {
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
