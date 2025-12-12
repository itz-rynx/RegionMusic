package org.rynx.regionMusic.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.rynx.regionMusic.RegionMusic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MusicToggleManager {
    
    private final RegionMusic plugin;
    private final Set<UUID> toggledOffPlayers = new HashSet<>();
    private File togglesFile;
    private FileConfiguration togglesConfig;
    
    public MusicToggleManager(RegionMusic plugin) {
        this.plugin = plugin;
        loadTogglesFile();
    }
    
    private void loadTogglesFile() {
        togglesFile = new File(plugin.getDataFolder(), "toggles.yml");
        if (!togglesFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                togglesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Không thể tạo file toggles.yml: " + e.getMessage());
            }
        }
        togglesConfig = YamlConfiguration.loadConfiguration(togglesFile);
    }
    
    public void loadToggles() {
        toggledOffPlayers.clear();
        if (togglesConfig.contains("toggled-off")) {
            List<String> uuidStrings = togglesConfig.getStringList("toggled-off");
            for (String uuidString : uuidStrings) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    toggledOffPlayers.add(uuid);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID không hợp lệ trong toggles.yml: " + uuidString);
                }
            }
        }
    }
    
    public void saveToggles() {
        List<String> uuidStrings = new ArrayList<>();
        for (UUID uuid : toggledOffPlayers) {
            uuidStrings.add(uuid.toString());
        }
        togglesConfig.set("toggled-off", uuidStrings);
        try {
            togglesConfig.save(togglesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể lưu file toggles.yml: " + e.getMessage());
        }
    }
    
    public boolean isMusicToggledOff(Player player) {
        return toggledOffPlayers.contains(player.getUniqueId());
    }
    
    public boolean isMusicToggledOff(UUID uuid) {
        return toggledOffPlayers.contains(uuid);
    }
    
    public void toggleMusic(Player player) {
        UUID uuid = player.getUniqueId();
        if (toggledOffPlayers.contains(uuid)) {
            toggledOffPlayers.remove(uuid);
        } else {
            toggledOffPlayers.add(uuid);
        }
        // Lưu ngay khi toggle thay đổi
        saveToggles();
    }
    
    public void setMusicOff(Player player, boolean off) {
        UUID uuid = player.getUniqueId();
        if (off) {
            toggledOffPlayers.add(uuid);
        } else {
            toggledOffPlayers.remove(uuid);
        }
        // Lưu ngay khi thay đổi
        saveToggles();
    }
    
    // Không còn remove player khi quit - giữ lại state để persist
    // public void removePlayer(Player player) {
    //     toggledOffPlayers.remove(player.getUniqueId());
    // }
}



