package io.github.phateio.residencealert;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Material.FIRE;
import static org.bukkit.Material.LAVA_BUCKET;

public class ResidenceAlert extends JavaPlugin implements Listener {

    private boolean includeServerOwner;
    private Set<String> alertResidenceOwner;
    private String alertMessage;
    private Set<Material> alertBlocks;

    @Override
    public void onEnable() {
        final FileConfiguration config = getConfig();

        final String path_include_server_owner = "include-server-owner";
        final String path_residence_owner_list = "alert-residence-owner";
        final String path_message = "alert-message";
        final String path_blocks = "alert-blocks-type-display";

        includeServerOwner = true;
        alertResidenceOwner = Collections.emptySet();
        alertMessage = "&c[Honeypot] player ${Player} placed ${Block} block at {world=${world}, x=${x}, y=${y}, z=${z}}";
        alertBlocks = new HashSet<>(Arrays.asList(LAVA_BUCKET, FIRE));

        config.addDefault(path_include_server_owner, includeServerOwner);
        config.addDefault(path_residence_owner_list, new ArrayList<>(alertResidenceOwner));
        config.addDefault(path_message, alertMessage);
        final List<String> blocksNameList = alertBlocks.stream().map(Material::name).collect(Collectors.toList());
        config.addDefault(path_blocks, blocksNameList);
        config.options().copyDefaults(true);

        includeServerOwner = config.getBoolean(path_include_server_owner, includeServerOwner);
        alertResidenceOwner = new HashSet<>(config.getStringList(path_include_server_owner));
        alertMessage = config.getString(path_message, alertMessage);
        alertBlocks = config.getStringList(path_blocks).stream().map(name -> {
            try {
                return Material.valueOf(name);
            } catch (IllegalArgumentException e) {
                getLogger().warning(name + " is not a Material value.");
                return null;
            }
        }).filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        saveConfig();

        residenceManagerAPI = Residence.getInstance().getResidenceManagerAPI();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private ResidenceInterface residenceManagerAPI;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        alertCheck(event.getPlayer(), event.getBlockPlaced().getType(), event.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        alertCheck(event.getPlayer(), event.getBucket(), event.getBlock().getLocation());
    }

    private void alertCheck(Player player, Material type, Location loc) {
        if (!alertBlocks.contains(type)) return;

        ClaimedResidence claim = residenceManagerAPI.getByLoc(loc);
        if (claim == null) return;

        final boolean alertServerLand = includeServerOwner && isServerLandWorkaround(claim);

        if (!alertServerLand && !alertResidenceOwner.contains(claim.getOwner())) return;

        final String output = alertMessage
                .replace("${Block}", type.name())
                .replace("${Player}", player.getName())
                .replace("${x}", loc.getBlockX() + "")
                .replace("${y}", loc.getBlockY() + "")
                .replace("${z}", loc.getBlockZ() + "")
                .replace("${world}", loc.getWorld().getName());

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', output));
    }

    private static boolean isServerLandWorkaround(ClaimedResidence claim) {
        return claim.getOwnerUUID().toString().equals(Residence.getInstance().getServerLandUUID());
    }
}
