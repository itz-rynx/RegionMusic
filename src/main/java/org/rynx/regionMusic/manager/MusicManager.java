package org.rynx.regionMusic.manager;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.rynx.regionMusic.RegionMusic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MusicManager {
    
    private final RegionMusic plugin;
    private final RegionConfigManager configManager;
    private final MusicToggleManager toggleManager;
    private final Map<UUID, BukkitTask> playerMusicTasks = new HashMap<>();
    // Track current song index và region cho mỗi player
    private final Map<UUID, Integer> playerCurrentSongIndex = new HashMap<>();
    private final Map<UUID, String> playerCurrentRegion = new HashMap<>();
    
    public MusicManager(RegionMusic plugin, RegionConfigManager configManager, MusicToggleManager toggleManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.toggleManager = toggleManager;
    }
    
    public void playMusicForPlayer(Player player, String regionName) {
        if (toggleManager.isMusicToggledOff(player)) {
            return;
        }
        
        List<String> musicList = configManager.getMusicListForRegion(regionName);
        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Kiểm tra xem đã có task đang chạy cho region này chưa
        String currentRegion = playerCurrentRegion.get(playerId);
        if (currentRegion != null && currentRegion.equalsIgnoreCase(regionName)) {
            // Đang phát cùng region, không cần phát lại
            return;
        }
        
        // DỪNG HOÀN TOÀN task cũ trước khi phát bài mới (tránh phát cùng lúc)
        stopMusicForPlayer(player);
        
        // Reset về bài đầu cho region mới
        playerCurrentSongIndex.put(playerId, 0);
        playerCurrentRegion.put(playerId, regionName);
        
        // Đợi một tick để đảm bảo task cũ đã được hủy hoàn toàn
        new BukkitRunnable() {
            @Override
            public void run() {
                // Kiểm tra lại xem player còn online và toggle vẫn bật
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    return;
                }
                
                // Kiểm tra lại xem player vẫn ở trong region
                String checkRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (checkRegion == null || !checkRegion.equalsIgnoreCase(regionName)) {
                    return;
                }
                
                // Kiểm tra lại xem đã có task khác đang chạy chưa
                if (playerMusicTasks.containsKey(playerId)) {
                    return; // Đã có task khác đang chạy
                }
                
                // Bắt đầu phát playlist (chỉ phát một bài tại một thời điểm)
                playNextSong(player, regionName, musicList, 0);
            }
        }.runTaskLater(plugin, 2L); // Tăng delay lên 2 ticks để đảm bảo
    }
    
    private void playNextSong(Player player, String regionName, List<String> musicList, int songIndex) {
        UUID playerId = player.getUniqueId();
        
        // Đảm bảo chỉ có một task đang chạy - hủy task cũ nếu có
        BukkitTask existingTask = playerMusicTasks.get(playerId);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
            playerMusicTasks.remove(playerId);
        }
        
        // Kiểm tra lại xem player còn online
        if (!player.isOnline()) {
            return;
        }
        
        // Xử lý loop: nếu vượt quá size thì quay về 0
        final int currentIndex = songIndex >= musicList.size() ? 0 : songIndex;
        
        String musicName = musicList.get(currentIndex);
        String soundName = configManager.getSoundForMusic(musicName);
        if (soundName == null) {
            // Nếu bài này không tồn tại, chuyển sang bài tiếp theo
            int nextIndex = (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
            playNextSong(player, regionName, musicList, nextIndex);
            return;
        }
        
        int interval = configManager.getIntervalForMusic(musicName);
        
        // Cập nhật index hiện tại
        playerCurrentSongIndex.put(playerId, currentIndex);
        playerCurrentRegion.put(playerId, regionName);
        
        // Phát bài nhạc hiện tại (CHỈ MỘT BÀI tại một thời điểm)
        playSound(player, soundName);
        
        // Tính toán index bài tiếp theo (loop nếu hết)
        final int nextSongIndex = (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
        
        // Tạo task để phát bài tiếp theo sau khi bài này kết thúc
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Kiểm tra lại xem task này vẫn còn hợp lệ
                BukkitTask currentTask = playerMusicTasks.get(playerId);
                if (currentTask != this) {
                    return; // Task đã bị thay thế
                }
                
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    playerMusicTasks.remove(playerId);
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    return;
                }
                
                // Check if player is still in the region
                String currentRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (currentRegion == null || !currentRegion.equalsIgnoreCase(regionName)) {
                    playerMusicTasks.remove(playerId);
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    return;
                }
                
                // Phát bài tiếp theo (loop lại nếu hết) - chỉ khi task này vẫn còn hợp lệ
                if (playerMusicTasks.get(playerId) == this) {
                    playNextSong(player, regionName, musicList, nextSongIndex);
                }
            }
        }.runTaskLater(plugin, interval * 20L);
        
        // Lưu task mới (thay thế task cũ nếu có)
        playerMusicTasks.put(playerId, task);
    }
    
    public void stopMusicForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = playerMusicTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        playerCurrentSongIndex.remove(uuid);
        playerCurrentRegion.remove(uuid);
    }
    
    private void playSound(Player player, String soundName) {
        org.bukkit.Location location = player.getLocation();
        
        // Paper 1.21.5 supports playSound with String for both vanilla and custom sounds
        // Format: "minecraft:sound_name" for vanilla or "namespace:sound_name" for custom
        try {
            // Try as-is first (supports both vanilla and custom sounds)
            player.playSound(location, soundName, 1.0f, 1.0f);
        } catch (Exception e) {
            // If direct string fails, try with minecraft: namespace prefix
            try {
                String formattedSound = soundName.contains(":") ? soundName : "minecraft:" + soundName.toLowerCase().replace("_", ".");
                player.playSound(location, formattedSound, 1.0f, 1.0f);
            } catch (Exception ex) {
                plugin.getLogger().warning("Không thể phát âm thanh: " + soundName + " - " + ex.getMessage());
            }
        }
    }
    
    public void skipToNextSong(Player player) {
        UUID playerId = player.getUniqueId();
        String currentRegion = playerCurrentRegion.get(playerId);
        
        if (currentRegion == null) {
            return; // Không có region đang phát
        }
        
        List<String> musicList = configManager.getMusicListForRegion(currentRegion);
        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        
        Integer currentIndex = playerCurrentSongIndex.get(playerId);
        if (currentIndex == null) {
            currentIndex = 0;
        }
        
        // Dừng nhạc hiện tại
        stopMusicForPlayer(player);
        
        // Phát bài tiếp theo
        int nextIndex = (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
        playNextSong(player, currentRegion, musicList, nextIndex);
    }
    
    public void removePlayer(Player player) {
        stopMusicForPlayer(player);
    }
}

