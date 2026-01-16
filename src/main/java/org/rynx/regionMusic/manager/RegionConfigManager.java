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
    // Map: regionName -> List of ambienceNames (hỗ trợ nhiều ambience trong 1 region)
    private final Map<String, List<String>> regionAmbienceMap = new HashMap<>();
    // Map: regionName -> playmode (sequential hoặc random)
    private final Map<String, String> regionPlayModeMap = new HashMap<>();
    // Map: musicName -> sound
    private final Map<String, String> musicSoundMap = new HashMap<>();
    // Map: musicName -> interval
    private final Map<String, Integer> musicIntervalMap = new HashMap<>();
    // Map: musicName -> display name
    private final Map<String, String> musicDisplayNameMap = new HashMap<>();
    // Map: musicName -> volume
    private final Map<String, Float> musicVolumeMap = new HashMap<>();
    // Map: musicName -> pitch
    private final Map<String, Float> musicPitchMap = new HashMap<>();
    
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
        regionAmbienceMap.clear();
        regionPlayModeMap.clear();
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
                
                // Load ambience (tùy chọn) - hỗ trợ cả single ambience và list of ambiences
                if (regionsConfig.contains("regions." + key + ".ambience")) {
                    if (regionsConfig.isList("regions." + key + ".ambience")) {
                        // Nhiều ambience trong 1 region
                        List<String> ambienceList = regionsConfig.getStringList("regions." + key + ".ambience");
                        if (regionName != null && !ambienceList.isEmpty()) {
                            regionAmbienceMap.put(regionName, new ArrayList<>(ambienceList));
                        }
                    } else {
                        // Single ambience
                        String ambienceName = regionsConfig.getString("regions." + key + ".ambience");
                        if (regionName != null && ambienceName != null) {
                            List<String> ambienceList = new ArrayList<>();
                            ambienceList.add(ambienceName);
                            regionAmbienceMap.put(regionName, ambienceList);
                        }
                    }
                }
                
                // Load playmode (sequential hoặc random)
                if (regionName != null && regionMusicMap.containsKey(regionName)) {
                    String playMode = regionsConfig.getString("regions." + key + ".playmode", "sequential");
                    // Chỉ chấp nhận sequential hoặc random
                    if (!playMode.equalsIgnoreCase("sequential") && !playMode.equalsIgnoreCase("random")) {
                        playMode = "sequential"; // Mặc định sequential nếu không hợp lệ
                    }
                    regionPlayModeMap.put(regionName, playMode.toLowerCase());
                }
            }
        }
    }
    
    private void loadMusics() {
        musicSoundMap.clear();
        musicIntervalMap.clear();
        musicDisplayNameMap.clear();
        musicVolumeMap.clear();
        musicPitchMap.clear();
        if (musicsConfig.contains("musics")) {
            Set<String> musics = musicsConfig.getConfigurationSection("musics").getKeys(false);
            for (String key : musics) {
                String sound = musicsConfig.getString("musics." + key + ".sound");
                int interval = musicsConfig.getInt("musics." + key + ".interval", 185);
                String displayName = musicsConfig.getString("musics." + key + ".name");
                float volume = (float) musicsConfig.getDouble("musics." + key + ".volume", 1.0);
                float pitch = (float) musicsConfig.getDouble("musics." + key + ".pitch", 1.0);
                if (sound != null) {
                    musicSoundMap.put(key, sound);
                    musicIntervalMap.put(key, interval);
                    // Nếu không có name tùy chỉnh, dùng tên key làm display name
                    musicDisplayNameMap.put(key, displayName != null ? displayName : key);
                    // Giới hạn volume trong khoảng 0.0 - 1.0
                    musicVolumeMap.put(key, Math.max(0.0f, Math.min(1.0f, volume)));
                    // Giới hạn pitch trong khoảng 0.5 - 2.0
                    musicPitchMap.put(key, Math.max(0.5f, Math.min(2.0f, pitch)));
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
    
    // Lấy danh sách ambience cho region (hỗ trợ nhiều ambience)
    public List<String> getAmbienceListForRegion(String regionName) {
        return regionAmbienceMap.getOrDefault(regionName, new ArrayList<>());
    }
    
    // Kiểm tra xem region có ambience không
    public boolean hasAmbience(String regionName) {
        return regionAmbienceMap.containsKey(regionName) && !regionAmbienceMap.get(regionName).isEmpty();
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
    
    public float getVolumeForMusic(String musicName) {
        return musicVolumeMap.getOrDefault(musicName, 1.0f);
    }
    
    public float getPitchForMusic(String musicName) {
        return musicPitchMap.getOrDefault(musicName, 1.0f);
    }
    
    public String getPlayModeForRegion(String regionName) {
        return regionPlayModeMap.getOrDefault(regionName, "sequential");
    }
    
    public boolean isRandomMode(String regionName) {
        return "random".equalsIgnoreCase(getPlayModeForRegion(regionName));
    }
    
    // Lấy tất cả regions được cấu hình
    public Map<String, List<String>> getAllRegions() {
        return new HashMap<String, List<String>>(regionMusicMap);
    }
    
    // Thêm song mới vào musics.yml
    public boolean addMusic(String musicName, String sound, int interval, String displayName, float volume, float pitch) {
        try {
            // Kiểm tra xem music đã tồn tại chưa
            if (hasMusic(musicName)) {
                return false; // Đã tồn tại
            }

            // Thêm vào config
            musicsConfig.set("musics." + musicName + ".sound", sound);
            musicsConfig.set("musics." + musicName + ".interval", interval);
            musicsConfig.set("musics." + musicName + ".name", displayName != null ? displayName : musicName);
            musicsConfig.set("musics." + musicName + ".volume", volume);
            musicsConfig.set("musics." + musicName + ".pitch", pitch);

            // Lưu file
            musicsConfig.save(musicsFile);

            // Reload để cập nhật trong memory
            loadMusics();

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Lỗi khi thêm music: " + e.getMessage());
            return false;
        }
    }

    // Chỉnh sửa song trong musics.yml
    public boolean editMusic(String musicName, String sound, int interval, String displayName, float volume, float pitch) {
        try {
            // Kiểm tra xem music có tồn tại không
            if (!hasMusic(musicName)) {
                return false; // Không tồn tại
            }

            // Cập nhật config
            musicsConfig.set("musics." + musicName + ".sound", sound);
            musicsConfig.set("musics." + musicName + ".interval", interval);
            musicsConfig.set("musics." + musicName + ".name", displayName != null ? displayName : musicName);
            musicsConfig.set("musics." + musicName + ".volume", volume);
            musicsConfig.set("musics." + musicName + ".pitch", pitch);

            // Lưu file
            musicsConfig.save(musicsFile);

            // Reload để cập nhật trong memory
            loadMusics();

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Lỗi khi chỉnh sửa music: " + e.getMessage());
            return false;
        }
    }

    // Xóa song khỏi musics.yml
    public boolean deleteMusic(String musicName) {
        try {
            // Kiểm tra xem music có tồn tại không
            if (!hasMusic(musicName)) {
                return false; // Không tồn tại
            }

            // Kiểm tra xem music có đang được sử dụng trong regions không
            if (isMusicUsedInRegions(musicName)) {
                return false; // Đang được sử dụng, không thể xóa
            }

            // Xóa khỏi config
            musicsConfig.set("musics." + musicName, null);

            // Lưu file
            musicsConfig.save(musicsFile);

            // Reload để cập nhật trong memory
            loadMusics();

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Lỗi khi xóa music: " + e.getMessage());
            return false;
        }
    }

    // Kiểm tra xem music có đang được sử dụng trong regions không
    public boolean isMusicUsedInRegions(String musicName) {
        for (List<String> musicList : regionMusicMap.values()) {
            if (musicList.contains(musicName)) {
                return true;
            }
        }
        for (List<String> ambienceList : regionAmbienceMap.values()) {
            if (ambienceList.contains(musicName)) {
                return true;
            }
        }
        return false;
    }

    // Lấy tất cả music names (để hiển thị trong GUI)
    public java.util.Set<String> getAllMusicNames() {
        return new java.util.HashSet<>(musicSoundMap.keySet());
    }

    // Lấy thông tin đầy đủ của một music (để chỉnh sửa)
    public java.util.Map<String, Object> getMusicInfo(String musicName) {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        if (!hasMusic(musicName)) {
            return info;
        }

        info.put("sound", musicSoundMap.get(musicName));
        info.put("interval", musicIntervalMap.get(musicName));
        info.put("displayName", musicDisplayNameMap.get(musicName));
        info.put("volume", musicVolumeMap.get(musicName));
        info.put("pitch", musicPitchMap.get(musicName));

        return info;
    }
}

