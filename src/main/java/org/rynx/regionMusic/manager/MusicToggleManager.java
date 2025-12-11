package org.rynx.regionMusic.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MusicToggleManager {
    
    private final Set<UUID> toggledOffPlayers = new HashSet<>();
    
    public boolean isMusicToggledOff(Player player) {
        return toggledOffPlayers.contains(player.getUniqueId());
    }
    
    public void toggleMusic(Player player) {
        UUID uuid = player.getUniqueId();
        if (toggledOffPlayers.contains(uuid)) {
            toggledOffPlayers.remove(uuid);
        } else {
            toggledOffPlayers.add(uuid);
        }
    }
    
    public void setMusicOff(Player player, boolean off) {
        UUID uuid = player.getUniqueId();
        if (off) {
            toggledOffPlayers.add(uuid);
        } else {
            toggledOffPlayers.remove(uuid);
        }
    }
    
    public void removePlayer(Player player) {
        toggledOffPlayers.remove(player.getUniqueId());
    }
}


