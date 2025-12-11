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
    private org.rynx.regionMusic.manager.MessageManager messageManager;
    private final Map<UUID, BukkitTask> playerMusicTasks = new HashMap<>();
    // Track current song index và region cho mỗi player
    private final Map<UUID, Integer> playerCurrentSongIndex = new HashMap<>();
    private final Map<UUID, String> playerCurrentRegion = new HashMap<>();
    // Track sound đang phát để có thể dừng khi skip
    private final Map<UUID, String> playerCurrentSound = new HashMap<>();
    
    public MusicManager(RegionMusic plugin, RegionConfigManager configManager, MusicToggleManager toggleManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.toggleManager = toggleManager;
        this.messageManager = plugin.getMessageManager();
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
            // Đang phát cùng region, không cần phát lại (tránh spam)
            return;
        }
        
        // DỪNG HOÀN TOÀN task cũ trước khi phát bài mới (tránh phát cùng lúc)
        BukkitTask oldTask = playerMusicTasks.get(playerId);
        if (oldTask != null && !oldTask.isCancelled()) {
            oldTask.cancel();
            playerMusicTasks.remove(playerId);
        }
        
        // Reset về bài đầu cho region mới
        playerCurrentSongIndex.put(playerId, 0);
        playerCurrentRegion.put(playerId, regionName);
        
        // Đợi một tick để đảm bảo task cũ đã được hủy hoàn toàn
        new BukkitRunnable() {
            @Override
            public void run() {
                // Kiểm tra lại xem player còn online và toggle vẫn bật
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    // Cleanup nếu player offline hoặc toggle tắt
                    playerMusicTasks.remove(playerId);
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    playerCurrentSound.remove(playerId);
                    return;
                }
                
                // Kiểm tra lại xem player vẫn ở trong region
                String checkRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (checkRegion == null || !checkRegion.equalsIgnoreCase(regionName)) {
                    // Player đã rời khỏi region, cleanup
                    playerMusicTasks.remove(playerId);
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    playerCurrentSound.remove(playerId);
                    return;
                }
                
                // Kiểm tra lại xem đã có task khác đang chạy chưa (double check)
                if (playerMusicTasks.containsKey(playerId)) {
                    return; // Đã có task khác đang chạy
                }
                
                // Bắt đầu phát playlist (chỉ phát một bài tại một thời điểm)
                playNextSong(player, regionName, musicList, 0);
            }
        }.runTaskLater(plugin, 2L); // Delay 2 ticks để đảm bảo task cũ đã được hủy
    }
    
    private void playNextSong(Player player, String regionName, List<String> musicList, int songIndex) {
        UUID playerId = player.getUniqueId();
        
        // Kiểm tra lại xem player còn online
        if (!player.isOnline()) {
            return;
        }
        
        // Kiểm tra lại xem player vẫn ở trong region
        String checkRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
        if (checkRegion == null || !checkRegion.equalsIgnoreCase(regionName)) {
            // Player đã rời khỏi region, dừng nhạc
            stopMusicForPlayer(player);
            return;
        }
        
        // Đảm bảo chỉ có một task đang chạy - hủy task cũ nếu có
        BukkitTask existingTask = playerMusicTasks.get(playerId);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
            playerMusicTasks.remove(playerId);
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
        
        // Lấy tên hiển thị của bài nhạc
        String displayName = configManager.getDisplayNameForMusic(musicName);
        
        // Hiển thị thông báo đang phát bài
        if (messageManager != null) {
            String message = messageManager.getMessage("now-playing", "{song}", displayName);
            if (message != null && !message.isEmpty()) {
                player.sendMessage(message);
            }
        }
        
        // Lấy volume và pitch từ config
        float volume = configManager.getVolumeForMusic(musicName);
        float pitch = configManager.getPitchForMusic(musicName);
        
        // Phát bài nhạc hiện tại (CHỈ MỘT BÀI tại một thời điểm)
        playSound(player, soundName, volume, pitch);
        
        // Tính toán index bài tiếp theo (loop nếu hết)
        final int nextSongIndex = (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
        
        // Tạo task để phát bài tiếp theo sau khi bài này kết thúc
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Xóa task khỏi map trước để tránh conflict
                playerMusicTasks.remove(playerId);
                
                // Kiểm tra lại xem player còn online và toggle vẫn bật
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    playerCurrentSound.remove(playerId);
                    return;
                }
                
                // Check if player is still in the region
                String currentRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (currentRegion == null || !currentRegion.equalsIgnoreCase(regionName)) {
                    playerCurrentSongIndex.remove(playerId);
                    playerCurrentRegion.remove(playerId);
                    playerCurrentSound.remove(playerId);
                    return;
                }
                
                // Phát bài tiếp theo tự động (loop lại nếu hết)
                playNextSong(player, regionName, musicList, nextSongIndex);
            }
        }.runTaskLater(plugin, interval * 20L);
        
        // Lưu task mới
        playerMusicTasks.put(playerId, task);
    }
    
    public void stopMusicForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = playerMusicTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        
        // Dừng sound đang phát
        String currentSound = playerCurrentSound.remove(uuid);
        if (currentSound != null) {
            stopSound(player, currentSound);
        }
        
        playerCurrentSongIndex.remove(uuid);
        playerCurrentRegion.remove(uuid);
    }
    
    private void playSound(Player player, String soundName, float volume, float pitch) {
        UUID playerId = player.getUniqueId();
        org.bukkit.Location location = player.getLocation();
        
        // Dừng sound cũ đang phát trước (nếu có)
        String oldSound = playerCurrentSound.get(playerId);
        if (oldSound != null) {
            stopSound(player, oldSound);
        }
        
        // Paper 1.21.5 supports playSound with String for both vanilla and custom sounds
        // Format: "minecraft:sound_name" for vanilla or "namespace:sound_name" for custom
        String actualSoundName = soundName;
        try {
            // Try as-is first (supports both vanilla and custom sounds)
            player.playSound(location, soundName, volume, pitch);
            actualSoundName = soundName;
        } catch (Exception e) {
            // If direct string fails, try with minecraft: namespace prefix
            try {
                String formattedSound = soundName.contains(":") ? soundName : "minecraft:" + soundName.toLowerCase().replace("_", ".");
                player.playSound(location, formattedSound, volume, pitch);
                actualSoundName = formattedSound;
            } catch (Exception ex) {
                plugin.getLogger().warning("Không thể phát âm thanh: " + soundName + " - " + ex.getMessage());
                return;
            }
        }
        
        // Lưu lại sound name đang phát để có thể dừng sau này
        playerCurrentSound.put(playerId, actualSoundName);
    }
    
    private void stopSound(Player player, String soundName) {
        try {
            // Thử dừng với sound name gốc
            player.stopSound(soundName);
        } catch (Exception e) {
            // Nếu không được, thử với format khác
            try {
                if (soundName.contains(":")) {
                    // Nếu có namespace, thử dừng với tên không có namespace
                    String nameWithoutNamespace = soundName.substring(soundName.indexOf(":") + 1);
                    player.stopSound(nameWithoutNamespace);
                } else {
                    // Nếu không có namespace, thử thêm minecraft:
                    String formattedSound = "minecraft:" + soundName.toLowerCase().replace("_", ".");
                    player.stopSound(formattedSound);
                }
            } catch (Exception ex) {
                // Nếu vẫn không được, dừng tất cả sounds (fallback)
                try {
                    player.stopAllSounds();
                } catch (Exception ex2) {
                    // Ignore nếu không thể dừng sound
                }
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
        
        // Hủy task hiện tại để dừng bài đang phát
        BukkitTask currentTask = playerMusicTasks.get(playerId);
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
            playerMusicTasks.remove(playerId);
        }
        
        // Dừng sound đang phát trước khi chuyển sang bài tiếp theo
        String currentSound = playerCurrentSound.get(playerId);
        if (currentSound != null) {
            stopSound(player, currentSound);
            playerCurrentSound.remove(playerId);
        }
        
        // Tính toán index bài tiếp theo
        int nextIndex = (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
        
        // Đợi một tick để đảm bảo task cũ và sound đã được dừng hoàn toàn
        new BukkitRunnable() {
            @Override
            public void run() {
                // Kiểm tra lại xem player còn online và vẫn ở trong region
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    return;
                }
                
                String checkRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (checkRegion == null || !checkRegion.equalsIgnoreCase(currentRegion)) {
                    return;
                }
                
                // Phát bài tiếp theo
                playNextSong(player, currentRegion, musicList, nextIndex);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        stopMusicForPlayer(player);
        // Đảm bảo cleanup hoàn toàn
        playerCurrentSound.remove(uuid);
    }
    
    /**
     * Kiểm tra xem có đang phát nhạc cho player không
     * @param player Player cần kiểm tra
     * @return true nếu đang phát nhạc, false nếu không
     */
    public boolean isMusicPlaying(Player player) {
        UUID playerId = player.getUniqueId();
        BukkitTask task = playerMusicTasks.get(playerId);
        return task != null && !task.isCancelled();
    }
    
    /**
     * Kiểm tra xem có đang phát nhạc cho region này không
     * @param player Player cần kiểm tra
     * @param regionName Tên region cần kiểm tra
     * @return true nếu đang phát nhạc cho region này, false nếu không
     */
    public boolean isMusicPlayingForRegion(Player player, String regionName) {
        UUID playerId = player.getUniqueId();
        String currentRegion = playerCurrentRegion.get(playerId);
        if (currentRegion == null || !currentRegion.equalsIgnoreCase(regionName)) {
            return false;
        }
        BukkitTask task = playerMusicTasks.get(playerId);
        return task != null && !task.isCancelled();
    }
}

