package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.CustomLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {
    
    private final RegionConfigManager configManager;
    private final CustomLogger customLogger;
    // Map để track player đang trong chế độ thêm song
    private final Map<UUID, String> addingMusicPlayers = new HashMap<UUID, String>();
    
    public ChatListener(RegionConfigManager configManager, CustomLogger customLogger) {
        this.configManager = configManager;
        this.customLogger = customLogger;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String message = event.getMessage();
        
        // Kiểm tra xem player có đang trong chế độ thêm song không
        if (addingMusicPlayers.containsKey(playerId)) {
            event.setCancelled(true); // Hủy chat message
            
            String musicName = addingMusicPlayers.get(playerId);
            
            // Xử lý message: format: "sound|interval|name|volume|pitch" hoặc chỉ "sound"
            String[] parts = message.split("\\|");
            String sound = parts[0].trim();
            int interval = 185; // Mặc định
            String displayName = musicName; // Mặc định dùng musicName
            float volume = 1.0f; // Mặc định
            float pitch = 1.0f; // Mặc định
            
            if (parts.length >= 2) {
                try {
                    interval = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    player.sendMessage("§cLỗi: Interval phải là số! Sử dụng mặc định: 185");
                }
            }
            if (parts.length >= 3) {
                displayName = parts[2].trim();
            }
            if (parts.length >= 4) {
                try {
                    volume = Float.parseFloat(parts[3].trim());
                    volume = Math.max(0.0f, Math.min(1.0f, volume));
                } catch (NumberFormatException e) {
                    player.sendMessage("§cLỗi: Volume phải là số! Sử dụng mặc định: 1.0");
                }
            }
            if (parts.length >= 5) {
                try {
                    pitch = Float.parseFloat(parts[4].trim());
                    pitch = Math.max(0.5f, Math.min(2.0f, pitch));
                } catch (NumberFormatException e) {
                    player.sendMessage("§cLỗi: Pitch phải là số! Sử dụng mặc định: 1.0");
                }
            }
            
            // Thêm song vào config
            boolean success = configManager.addMusic(musicName, sound, interval, displayName, volume, pitch);
            
            if (success) {
                // Log đẹp với tên Rynx to
                customLogger.info("§aĐã thêm bài hát mới: §e" + musicName + " §7(Sound: §f" + sound + "§7)");
                player.sendMessage("§a§l✓ Đã thêm bài hát: §e" + displayName + " §7(" + musicName + ")");
            } else {
                player.sendMessage("§c§l✗ Lỗi: Bài hát §e" + musicName + " §cđã tồn tại hoặc có lỗi xảy ra!");
            }
            
            // Xóa khỏi map
            addingMusicPlayers.remove(playerId);
        }
    }
    
    // Method để bắt đầu chế độ thêm song
    public void startAddingMusic(Player player, String musicName) {
        addingMusicPlayers.put(player.getUniqueId(), musicName);
        player.sendMessage("§6§l[RegionMusic] §7Nhập thông tin bài hát vào chat:");
        player.sendMessage("§7Format: §esound|interval|name|volume|pitch");
        player.sendMessage("§7Ví dụ: §eMUSIC_DISC_CAT|185|Spawn Theme|1.0|1.0");
        player.sendMessage("§7Hoặc chỉ nhập: §esound §7(để dùng giá trị mặc định)");
        player.sendMessage("§7Gõ §c/cancel §7để hủy");
    }
    
    // Method để hủy chế độ thêm song
    public void cancelAddingMusic(Player player) {
        if (addingMusicPlayers.remove(player.getUniqueId()) != null) {
            player.sendMessage("§c§l✗ Đã hủy thêm bài hát!");
        }
    }
    
    public boolean isAddingMusic(Player player) {
        return addingMusicPlayers.containsKey(player.getUniqueId());
    }
}

