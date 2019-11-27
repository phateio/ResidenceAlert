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

        final boolean alertServerLand = includeServerLand && isServerLandWorkaround(claim);

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
