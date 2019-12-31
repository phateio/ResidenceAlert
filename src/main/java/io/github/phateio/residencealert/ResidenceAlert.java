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

import static io.github.phateio.residencealert.Setting.*;

public class ResidenceAlert extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        final FileConfiguration config = getConfig();
        Setting.syncFile(getLogger(), config);
        saveConfig();

        residenceManagerAPI = Residence.getInstance().getResidenceManagerAPI();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private ResidenceInterface residenceManagerAPI;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        alertCheck(event.getPlayer(), event.getBlockAgainst().getType(), event.getBlockPlaced().getType(), event.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        alertCheck(event.getPlayer(), event.getBlockClicked().getType(), event.getBucket(), event.getBlock().getLocation());
    }

    private void alertCheck(Player player, Material clicked, Material type, Location loc) {
        if (loc.getBlockY() < ignoreYBelow) return;
        if (disabledWorlds.contains(loc.getWorld().getName())) return;
        if (!alertBlocks.contains(type)) return;
        if (checkFaces.stream()
                .map(it -> loc.clone().add(it.getDirection()).getBlock().getType())
                .anyMatch(ignoreNearbyBlocks::contains)
        ) return;

        ClaimedResidence claim = residenceManagerAPI.getByLoc(loc);
        if (claim == null) return;

        if (!(includeServerLand && isServerLandWorkaround(claim)) // not serverLand check
                && !alertResidenceOwner.contains(claim.getOwner()) // owner not in whitelist check
                && isNotEnabledClaim(claim.getName())
        ) return;

        final String output = alertMessage
                .replace("${Block}", type.name())
                .replace("${onClicked}", clicked.name())
                .replace("${Player}", player.getName())
                .replace("${x}", loc.getBlockX() + "")
                .replace("${y}", loc.getBlockY() + "")
                .replace("${z}", loc.getBlockZ() + "")
                .replace("${world}", loc.getWorld().getName());

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', output));
    }

    private static boolean isNotEnabledClaim(String claimName) {
        // check "Spawn2014.village.sub1.sub2"
        // in ["Spawn2014.village", "Spawn2018.village"]
        return enabledClaims.stream().noneMatch(claimName::startsWith);
    }

    private static boolean isServerLandWorkaround(ClaimedResidence claim) {
        return claim.getOwnerUUID().toString().equals(Residence.getInstance().getServerLandUUID());
    }
}
