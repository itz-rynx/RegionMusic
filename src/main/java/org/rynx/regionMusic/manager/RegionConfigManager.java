package org.rynx.regionMusic.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.rynx.regionMusic.RegionMusic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionConfigManager {
    
    private final RegionMusic plugin;
    private File regionsFile;
    private File musicsFile;
    private FileConfiguration regionsConfig;
    private FileConfiguration musicsConfig;
    
    // Map: regionName -> List of musicNames (hỗ trợ nhiều nhạc trong 1 region)
    private final Map<String, List<String>> regionMusicMap = new HashMap<>();
    // Map: musicName -> sound
    private final Map<String, String> musicSoundMap = new HashMap<>();
    // Map: musicName -> interval
    private final Map<String, Integer> musicIntervalMap = new HashMap<>();
    // Map: musicName -> display name
    private final Map<String, String> musicDisplayNameMap = new HashMap<>();
    
    public RegionConfigManager(RegionMusic plugin) {
        this.plugin = plugin;
        loadConfigs();
    }
    
    public void loadConfigs() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        regionsFile = new File(plugin.getDataFolder(), "regions.yml");
        musicsFile = new File(plugin.getDataFolder(), "musics.yml");
        
        if (!regionsFile.exists()) {
            plugin.saveResource("regions.yml", false);
        }
        if (!musicsFile.exists()) {
            plugin.saveResource("musics.yml", false);
        }
        
        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
        musicsConfig = YamlConfiguration.loadConfiguration(musicsFile);
        
        loadRegions();
        loadMusics();
    }
    
    private void loadRegions() {
        regionMusicMap.clear();
        if (regionsConfig.contains("regions")) {
            Set<String> regions = regionsConfig.getConfigurationSection("regions").getKeys(false);
            for (String key : regions) {
                String regionName = regionsConfig.getString("regions." + key + ".regionname");
                
                // Hỗ trợ cả single music và list of musics
                if (regionsConfig.isList("regions." + key + ".music")) {
                    // Nhiều nhạc trong 1 region
                    List<String> musicList = regionsConfig.getStringList("regions." + key + ".music");
                    if (regionName != null && !musicList.isEmpty()) {
                        regionMusicMap.put(regionName, new ArrayList<>(musicList));
                    }
                } else {
                    // Backward compatible: single music
                    String musicName = regionsConfig.getString("regions." + key + ".music");
                    if (regionName != null && musicName != null) {
                        List<String> musicList = new ArrayList<>();
                        musicList.add(musicName);
                        regionMusicMap.put(regionName, musicList);
                    }
                }
            }
        }
    }
    
    private void loadMusics() {
        musicSoundMap.clear();
        musicIntervalMap.clear();
        musicDisplayNameMap.clear();
        if (musicsConfig.contains("musics")) {
            Set<String> musics = musicsConfig.getConfigurationSection("musics").getKeys(false);
            for (String key : musics) {
                String sound = musicsConfig.getString("musics." + key + ".sound");
                int interval = musicsConfig.getInt("musics." + key + ".interval", 185);
                String displayName = musicsConfig.getString("musics." + key + ".name");
                if (sound != null) {
                    musicSoundMap.put(key, sound);
                    musicIntervalMap.put(key, interval);
                    // Nếu không có name tùy chỉnh, dùng tên key làm display name
                    musicDisplayNameMap.put(key, displayName != null ? displayName : key);
                }
            }
        }
    }
    
    public void reload() {
        loadConfigs();
    }
    
    // Lấy danh sách nhạc cho region (hỗ trợ nhiều nhạc)
    public List<String> getMusicListForRegion(String regionName) {
        return regionMusicMap.getOrDefault(regionName, new ArrayList<>());
    }
    
    // Backward compatible: lấy nhạc đầu tiên
    public String getMusicForRegion(String regionName) {
        List<String> musicList = getMusicListForRegion(regionName);
        return musicList.isEmpty() ? null : musicList.get(0);
    }
    
    public String getSoundForMusic(String musicName) {
        return musicSoundMap.get(musicName);
    }
    
    public int getIntervalForMusic(String musicName) {
        return musicIntervalMap.getOrDefault(musicName, 185);
    }
    
    public boolean hasRegion(String regionName) {
        return regionMusicMap.containsKey(regionName) && !regionMusicMap.get(regionName).isEmpty();
    }
    
    public boolean hasMusic(String musicName) {
        return musicSoundMap.containsKey(musicName);
    }
    
    public String getDisplayNameForMusic(String musicName) {
        return musicDisplayNameMap.getOrDefault(musicName, musicName);
    }
}

