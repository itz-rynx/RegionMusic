package org.rynx.regionMusic.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.rynx.regionMusic.RegionMusic;

import java.io.File;

public class MessageManager {
    
    private final RegionMusic plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;
    
    public MessageManager(RegionMusic plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    public void loadMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        messagesFile = new File(plugin.getDataFolder(), "lang.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public void reload() {
        loadMessages();
    }
    
    public String getMessage(String key) {
        String message = messagesConfig.getString("messages." + key);
        if (message == null) {
            return "§cMessage not found: " + key;
        }
        return message.replace("&", "§");
    }
    
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
}

