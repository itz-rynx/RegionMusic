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
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            String currentRegion = WorldGuardUtils.getPlayerRegion(player);
            if (currentRegion != null && configManager.hasRegion(currentRegion)) {
                musicManager.playMusicForPlayer(player, currentRegion);
            }
        }, 20L); // Wait 1 second for WorldGuard to initialize
    }
}


