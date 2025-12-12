package org.rynx.regionMusic.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.rynx.regionMusic.RegionMusic;

import java.io.File;

public class ConfigManager {
    
    private final RegionMusic plugin;
    private File configFile;
    private FileConfiguration config;
    private String version;
    private boolean debug;
    
    public ConfigManager(RegionMusic plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Load version và debug
        version = config.getString("version", plugin.getDescription().getVersion());
        debug = config.getBoolean("debug", false);
        
        // Cập nhật version trong config nếu khác với plugin.yml
        String pluginVersion = plugin.getDescription().getVersion();
        if (!version.equals(pluginVersion)) {
            config.set("version", pluginVersion);
            version = pluginVersion;
            try {
                config.save(configFile);
            } catch (Exception e) {
                plugin.getLogger().warning("Không thể lưu config.yml: " + e.getMessage());
            }
        }
    }
    
    public void reload() {
        loadConfig();
    }
    
    public String getVersion() {
        return version;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}

