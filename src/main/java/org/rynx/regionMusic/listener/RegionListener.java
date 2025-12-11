package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionListener implements Listener {
    
    private final RegionMusic plugin;
    private final MusicManager musicManager;
    private final MusicToggleManager toggleManager;
    private final RegionConfigManager configManager;
    // Track current region for each player to prevent spam (chỉ kích hoạt một lần khi vào/ra)
    private final Map<UUID, String> playerCurrentRegions = new HashMap<>();
    // Track region vừa ra khỏi và thời gian để tránh phát lại khi vào lại quá nhanh
    private final Map<UUID, String> playerLeftRegions = new HashMap<>();
    private final Map<UUID, Long> playerLeftRegionTime = new HashMap<>();
    
    public RegionListener(RegionMusic plugin, MusicManager musicManager, RegionConfigManager configManager) {
        this.plugin = plugin;
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
            if (previousRegion == null || !currentRegion.equalsIgnoreCase(previousRegion)) {
                // Kiểm tra xem có đang phát nhạc cho region này không (tránh spam khi vào lại)
                if (musicManager.isMusicPlayingForRegion(player, currentRegion)) {
                    // Đang phát nhạc cho region này rồi, không phát lại
                    playerCurrentRegions.put(playerId, currentRegion);
                    // Xóa thông tin ra khỏi region nếu có
                    playerLeftRegions.remove(playerId);
                    playerLeftRegionTime.remove(playerId);
                    return;
                }
                
                // Kiểm tra xem có vừa mới ra khỏi region này không (trong vòng 2 giây)
                String leftRegion = playerLeftRegions.get(playerId);
                Long leftTime = playerLeftRegionTime.get(playerId);
                if (leftRegion != null && leftRegion.equalsIgnoreCase(currentRegion) && leftTime != null) {
                    long timeSinceLeft = System.currentTimeMillis() - leftTime;
                    if (timeSinceLeft < 2000) { // 2 giây
                        // Vừa mới ra khỏi region này, không phát lại (tránh spam)
                        playerCurrentRegions.put(playerId, currentRegion);
                        playerLeftRegions.remove(playerId);
                        playerLeftRegionTime.remove(playerId);
                        return;
                    }
                }
                
                // Phát nhạc cho region mới hoặc vào lại sau khi đã dừng đủ lâu
                musicManager.playMusicForPlayer(player, currentRegion);
                playerCurrentRegions.put(playerId, currentRegion);
                // Xóa thông tin ra khỏi region nếu có
                playerLeftRegions.remove(playerId);
                playerLeftRegionTime.remove(playerId);
            }
        } else {
            // Player left region or not in any configured region
            if (previousRegion != null) {
                // Chỉ dừng khi thực sự rời khỏi region (tránh spam khi di chuyển nhanh)
                musicManager.stopMusicForPlayer(player);
                playerCurrentRegions.remove(playerId);
                // Lưu region vừa ra khỏi và thời gian để tránh phát lại khi vào lại quá nhanh
                playerLeftRegions.put(playerId, previousRegion);
                playerLeftRegionTime.put(playerId, System.currentTimeMillis());
                
                // Cleanup sau 5 giây để tránh memory leak
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerLeftRegions.remove(playerId);
                        playerLeftRegionTime.remove(playerId);
                    }
                }.runTaskLater(plugin, 100L); // 5 giây = 100 ticks
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup khi player rời để tránh memory leak
        UUID playerId = event.getPlayer().getUniqueId();
        playerCurrentRegions.remove(playerId);
        playerLeftRegions.remove(playerId);
        playerLeftRegionTime.remove(playerId);
    }
    
    // Method để clear region tracking khi stopmusic được gọi
    public void clearPlayerRegion(Player player) {
        UUID playerId = player.getUniqueId();
        playerCurrentRegions.remove(playerId);
        playerLeftRegions.remove(playerId);
        playerLeftRegionTime.remove(playerId);
    }
}

