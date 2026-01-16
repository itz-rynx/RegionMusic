package org.rynx.regionMusic.manager;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.rynx.regionMusic.RegionMusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MusicManager {
    
    private final RegionMusic plugin;
    private final RegionConfigManager configManager;
    private final MusicToggleManager toggleManager;
    private org.rynx.regionMusic.manager.MessageManager messageManager;
    private final Random random = new Random();
    private final Map<UUID, BukkitTask> playerMusicTasks = new HashMap<>();
    // Track current song index và region cho mỗi player
    private final Map<UUID, Integer> playerCurrentSongIndex = new HashMap<>();
    private final Map<UUID, String> playerCurrentRegion = new HashMap<>();
    // Track sound đang phát để có thể dừng khi skip (chỉ cho music, không ảnh hưởng ambience)
    private final Map<UUID, String> playerCurrentSound = new HashMap<>();
    // Track ambience tasks và sounds (riêng biệt với music)
    // Map: playerId -> List of ambience tasks (hỗ trợ nhiều ambience cùng lúc)
    private final Map<UUID, List<BukkitTask>> playerAmbienceTasks = new HashMap<>();
    private final Map<UUID, List<String>> playerCurrentAmbienceSounds = new HashMap<>();
    
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
                
                // Phát ambience nếu có (phát cùng lúc với music)
                playAmbienceForPlayer(player, regionName);
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
            int nextIndex = getNextSongIndex(regionName, musicList, currentIndex);
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
        
        // Tính toán index bài tiếp theo dựa trên playmode (sequential hoặc random)
        final int nextSongIndex = getNextSongIndex(regionName, musicList, currentIndex);
        
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
        
        // Dừng music sound đang phát
        String currentSound = playerCurrentSound.remove(uuid);
        if (currentSound != null) {
            stopSound(player, currentSound);
        }
        
        // Dừng ambience
        stopAmbienceForPlayer(player);
        
        playerCurrentSongIndex.remove(uuid);
        playerCurrentRegion.remove(uuid);
    }
    
    private void playSound(Player player, String soundName, float volume, float pitch) {
        UUID playerId = player.getUniqueId();
        org.bukkit.Location location = player.getLocation();
        
        // Dừng sound cũ đang phát trước (nếu có) - CHỈ dừng music sound, không dừng ambience
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
        
        // Tính toán index bài tiếp theo dựa trên playmode
        int nextIndex = getNextSongIndex(currentRegion, musicList, currentIndex);
        
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
    
    /**
     * Tính toán index bài tiếp theo dựa trên playmode (sequential hoặc random)
     */
    private int getNextSongIndex(String regionName, List<String> musicList, int currentIndex) {
        if (musicList.isEmpty()) {
            return 0;
        }
        
        if (musicList.size() == 1) {
            return 0; // Chỉ có 1 bài, luôn phát lại bài đó
        }
        
        boolean isRandom = configManager.isRandomMode(regionName);
        
        if (isRandom) {
            // Random mode: chọn ngẫu nhiên bài tiếp theo (không trùng với bài hiện tại)
            int randomIndex;
            do {
                randomIndex = random.nextInt(musicList.size());
            } while (randomIndex == currentIndex && musicList.size() > 1);
            return randomIndex;
        } else {
            // Sequential mode: phát tuần tự
            return (currentIndex + 1) >= musicList.size() ? 0 : (currentIndex + 1);
        }
    }
    
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        stopMusicForPlayer(player);
        // Đảm bảo cleanup hoàn toàn
        playerCurrentSound.remove(uuid);
        playerCurrentAmbienceSounds.remove(uuid);
    }
    
    /**
     * Phát ambience cho player trong region (phát liên tục, loop)
     * Ambience phát cùng lúc với music, không bị ảnh hưởng khi music thay đổi
     */
    private void playAmbienceForPlayer(Player player, String regionName) {
        if (toggleManager.isMusicToggledOff(player)) {
            return;
        }
        
        List<String> ambienceList = configManager.getAmbienceListForRegion(regionName);
        if (ambienceList == null || ambienceList.isEmpty()) {
            return; // Không có ambience cho region này
        }
        
        UUID playerId = player.getUniqueId();
        
        // Dừng ambience cũ nếu có (khi chuyển region)
        stopAmbienceForPlayer(player);
        
        // Lưu danh sách ambience sounds đang phát
        List<String> currentAmbienceSounds = new ArrayList<>();
        playerCurrentAmbienceSounds.put(playerId, currentAmbienceSounds);
        
        // Lưu danh sách ambience tasks
        List<BukkitTask> ambienceTasks = new ArrayList<>();
        playerAmbienceTasks.put(playerId, ambienceTasks);
        
        // Phát tất cả ambience sounds trong danh sách
        for (String ambienceName : ambienceList) {
            String soundName = configManager.getSoundForMusic(ambienceName);
            if (soundName == null) {
                continue; // Bỏ qua nếu không tìm thấy sound
            }
            
            float volume = configManager.getVolumeForMusic(ambienceName);
            float pitch = configManager.getPitchForMusic(ambienceName);
            int interval = configManager.getIntervalForMusic(ambienceName);
            
            // Phát ambience sound lần đầu
            playAmbienceSound(player, soundName, volume, pitch);
            currentAmbienceSounds.add(soundName);
            
            // Tạo task để loop ambience (phát lại sau khi kết thúc)
            final String finalSoundName = soundName;
            final float finalVolume = volume;
            final float finalPitch = pitch;
            final int finalInterval = interval;
            
            BukkitTask ambienceTask = createAmbienceLoopTask(player, regionName, finalSoundName, finalVolume, finalPitch, finalInterval);
            ambienceTasks.add(ambienceTask);
        }
    }
    
    /**
     * Phát ambience sound (không dừng sound cũ, cho phép nhiều ambience cùng lúc)
     */
    private void playAmbienceSound(Player player, String soundName, float volume, float pitch) {
        if (!player.isOnline()) {
            return;
        }
        
        org.bukkit.Location location = player.getLocation();
        
        try {
            // Try as-is first (supports both vanilla and custom sounds)
            player.playSound(location, soundName, volume, pitch);
        } catch (Exception e) {
            // If direct string fails, try with minecraft: namespace prefix
            try {
                String formattedSound = soundName.contains(":") ? soundName : "minecraft:" + soundName.toLowerCase().replace("_", ".");
                player.playSound(location, formattedSound, volume, pitch);
            } catch (Exception ex) {
                plugin.getLogger().warning("Không thể phát ambience sound: " + soundName + " - " + ex.getMessage());
                return;
            }
        }
    }
    
    /**
     * Tạo task để loop ambience (phát lại sau mỗi interval)
     */
    private BukkitTask createAmbienceLoopTask(Player player, String regionName, String soundName, float volume, float pitch, int interval) {
        UUID playerId = player.getUniqueId();
        
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Kiểm tra lại xem player còn online và vẫn ở trong region
                if (!player.isOnline() || toggleManager.isMusicToggledOff(player)) {
                    stopAmbienceForPlayer(player);
                    return;
                }
                
                String checkRegion = org.rynx.regionMusic.util.WorldGuardUtils.getPlayerRegion(player);
                if (checkRegion == null || !checkRegion.equalsIgnoreCase(regionName)) {
                    stopAmbienceForPlayer(player);
                    return;
                }
                
                // Kiểm tra xem ambience vẫn còn trong danh sách không
                if (!playerCurrentAmbienceSounds.containsKey(playerId) || 
                    !playerCurrentAmbienceSounds.get(playerId).contains(soundName)) {
                    return; // Ambience đã bị dừng
                }
                
                // Phát lại ambience (loop)
                playAmbienceSound(player, soundName, volume, pitch);
                
                // Lên lịch phát lại (tạo task mới để loop)
                BukkitTask newTask = createAmbienceLoopTask(player, regionName, soundName, volume, pitch, interval);
                
                // Thêm task mới vào list (task cũ sẽ tự động bị hủy khi kết thúc)
                List<BukkitTask> tasks = playerAmbienceTasks.get(playerId);
                if (tasks != null) {
                    tasks.add(newTask);
                }
            }
        }.runTaskLater(plugin, interval * 20L);
        
        return task;
    }
    
    /**
     * Dừng tất cả ambience cho player
     */
    private void stopAmbienceForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Dừng tất cả ambience tasks
        List<BukkitTask> ambienceTasks = playerAmbienceTasks.remove(uuid);
        if (ambienceTasks != null) {
            for (BukkitTask task : ambienceTasks) {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }
        }
        
        // Dừng tất cả ambience sounds
        List<String> ambienceSounds = playerCurrentAmbienceSounds.remove(uuid);
        if (ambienceSounds != null) {
            for (String soundName : ambienceSounds) {
                stopSound(player, soundName);
            }
        }
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

