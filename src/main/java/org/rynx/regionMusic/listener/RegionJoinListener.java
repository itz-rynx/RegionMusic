package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

public class RegionJoinListener implements Listener {
    
    private final RegionMusic plugin;
    private final MusicManager musicManager;
    private final RegionConfigManager configManager;
    
    public RegionJoinListener(RegionMusic plugin, MusicManager musicManager, RegionConfigManager configManager) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.configManager = configManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is in a region when they join (chỉ kích hoạt một lần)
        // Delay lâu hơn để tránh conflict với RegionListener
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            
            // Kiểm tra lại xem player đã được RegionListener xử lý chưa
            String currentRegion = WorldGuardUtils.getPlayerRegion(player);
            if (currentRegion != null && configManager.hasRegion(currentRegion)) {
                // Chỉ phát nếu player vẫn ở trong region sau khi join
                musicManager.playMusicForPlayer(player, currentRegion);
            }
        }, 40L); // Wait 2 seconds for WorldGuard to initialize và RegionListener xử lý
    }
}


