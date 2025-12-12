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
    // Không còn cần toggleManager vì toggle state được persist và không xóa khi quit
    // private final MusicToggleManager toggleManager;
    
    public PlayerQuitListener(RegionMusic plugin, MusicManager musicManager, MusicToggleManager toggleManager) {
        this.musicManager = musicManager;
        // this.toggleManager = toggleManager;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Cleanup: dừng nhạc khi player rời
        musicManager.removePlayer(player);
        // Không xóa toggle state - giữ lại để persist qua các lần vào/ra và restart
        // toggleManager.removePlayer(player);
    }
}

