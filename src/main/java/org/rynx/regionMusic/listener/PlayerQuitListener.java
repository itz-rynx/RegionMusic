package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;

public class PlayerQuitListener implements Listener {
    
    private final MusicManager musicManager;
    private final MusicToggleManager toggleManager;
    
    public PlayerQuitListener(RegionMusic plugin, MusicManager musicManager, MusicToggleManager toggleManager) {
        this.musicManager = musicManager;
        this.toggleManager = toggleManager;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Cleanup: dừng nhạc và xóa dữ liệu khi player rời
        musicManager.removePlayer(player);
        toggleManager.removePlayer(player);
    }
}

