package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionListener implements Listener {
    
    private final MusicManager musicManager;
    private final MusicToggleManager toggleManager;
    private final RegionConfigManager configManager;
    // Track current region for each player to prevent spam (chỉ kích hoạt một lần khi vào/ra)
    private final Map<UUID, String> playerCurrentRegions = new HashMap<>();
    
    public RegionListener(RegionMusic plugin, MusicManager musicManager, RegionConfigManager configManager) {
        this.musicManager = musicManager;
        this.toggleManager = plugin.getMusicToggleManager();
        this.configManager = configManager;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Kiểm tra toggle state - nếu đã tắt thì không làm gì
        if (toggleManager.isMusicToggledOff(player)) {
            return;
        }
        
        // Only check if player moved to a different block (tối ưu hiệu năng)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        String previousRegion = playerCurrentRegions.get(playerId);
        String currentRegion = WorldGuardUtils.getPlayerRegion(player);
        
        // Chỉ kích hoạt một lần mỗi khi vào/ra (không gây spam!)
        if (currentRegion != null && configManager.hasRegion(currentRegion)) {
            // Player entered a configured region
            if (!currentRegion.equalsIgnoreCase(previousRegion)) {
                musicManager.playMusicForPlayer(player, currentRegion);
                playerCurrentRegions.put(playerId, currentRegion);
            }
        } else {
            // Player left region or not in any configured region
            if (previousRegion != null) {
                musicManager.stopMusicForPlayer(player);
                playerCurrentRegions.remove(playerId);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup khi player rời để tránh memory leak
        playerCurrentRegions.remove(event.getPlayer().getUniqueId());
    }
    
    // Method để clear region tracking khi stopmusic được gọi
    public void clearPlayerRegion(Player player) {
        playerCurrentRegions.remove(player.getUniqueId());
    }
}

