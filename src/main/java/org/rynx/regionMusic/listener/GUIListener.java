package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.gui.SongManagementGUI;
import org.rynx.regionMusic.manager.RegionConfigManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUIListener implements Listener {

    private final RegionMusic plugin;
    private final RegionConfigManager configManager;
    private final SongManagementGUI songGUI;

    public GUIListener(RegionMusic plugin, RegionConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.songGUI = new SongManagementGUI(configManager);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Handle RegionMusic main GUI
        if (title.startsWith("§6§lRegionMusic §7- §e")) {
            event.setCancelled(true);
            handleRegionMusicGUIClick(event, player);
        }
        // Handle Song Management GUI
        else if (title.startsWith("§6§lQuản lý bài hát")) {
            event.setCancelled(true);
            handleSongManagementGUIClick(event, player);
        }
        // Handle Edit Song GUI
        else if (title.startsWith("§6§lChỉnh sửa: §e")) {
            event.setCancelled(true);
            handleEditSongGUIClick(event, player, title);
        }
        // Handle Delete Confirmation GUI
        else if (title.startsWith("§c§lXác nhận xóa: §e")) {
            event.setCancelled(true);
            handleDeleteConfirmGUIClick(event, player, title);
        }
    }

    private void handleRegionMusicGUIClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Close button
        if (itemName.equals("§c§lĐóng")) {
            player.closeInventory();
        }
    }

    private void handleSongManagementGUIClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Close button
        if (itemName.equals("§c§l✕ Đóng")) {
            player.closeInventory();
            return;
        }

        // Add new song button
        if (itemName.equals("§a§l➕ Thêm bài hát mới")) {
            player.closeInventory();
            player.sendMessage("§6§l[RegionMusic] §7Enter new song information:");
            player.sendMessage("§7Format: §e/addmusic <song_name>");
            player.sendMessage("§7Example: §e/addmusic mymusic");
            return;
        }

        // Navigation buttons
        if (itemName.equals("§e§lTrang trước")) {
            int currentPage = getCurrentPageFromTitle(event.getView().getTitle());
            songGUI.openGUI(player, currentPage - 1);
            return;
        }

        if (itemName.equals("§e§lTrang tiếp theo")) {
            int currentPage = getCurrentPageFromTitle(event.getView().getTitle());
            songGUI.openGUI(player, currentPage + 1);
            return;
        }

        // Music items (first 28 slots are music items)
        if (slot >= 0 && slot < 28) {
            String musicName = getMusicNameFromItemLore(event.getCurrentItem());
            if (musicName != null) {
                // Left click = edit, Right click = delete
                if (event.isLeftClick()) {
                    songGUI.openEditGUI(player, musicName);
                } else if (event.isRightClick()) {
                    if (!configManager.isMusicUsedInRegions(musicName)) {
                        songGUI.openDeleteConfirmGUI(player, musicName);
                    } else {
                        player.sendMessage("§c§l✗ Cannot delete song currently used in regions!");
                    }
                }
            }
        }
    }

    private void handleEditSongGUIClick(InventoryClickEvent event, Player player, String title) {
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Close button
        if (itemName.equals("§c§l✕ Đóng")) {
            player.closeInventory();
            songGUI.openGUI(player);
            return;
        }

        // Back button
        if (itemName.equals("§e§l← Quay lại")) {
            player.closeInventory();
            songGUI.openGUI(player);
            return;
        }

        // Edit instructions item - send command to player
        if (itemName.equals("§e§lHướng dẫn chỉnh sửa")) {
            String musicName = extractMusicNameFromTitle(title);
            player.closeInventory();
            player.sendMessage("§6§l[RegionMusic] §7To edit song §e" + musicName + "§7:");
            player.sendMessage("§7Type command: §e/editmusic " + musicName + " <sound|interval|name|volume|pitch>");
            player.sendMessage("§7Example: §e/editmusic " + musicName + " MUSIC_DISC_CAT|185|New Name|1.0|1.0");
            player.sendMessage("§7Type §c/cancel §7to cancel");
            return;
        }
    }

    private void handleDeleteConfirmGUIClick(InventoryClickEvent event, Player player, String title) {
        if (event.getCurrentItem() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String musicName = extractMusicNameFromTitle(title);

        // Confirm delete
        if (itemName.equals("§c§l✅ XÁC NHẬN XÓA")) {
            boolean success = configManager.deleteMusic(musicName);
            player.closeInventory();

            if (success) {
                player.sendMessage("§a§l✓ Song deleted: §e" + musicName);
                plugin.getCustomLogger().info("§aSong deleted: §e" + musicName + " §7(by " + player.getName() + ")");
            } else {
                player.sendMessage("§c§l✗ Error: Cannot delete song §e" + musicName);
            }

            // Reopen management GUI
            songGUI.openGUI(player);
            return;
        }

        // Cancel delete
        if (itemName.equals("§a§l❌ HỦY")) {
            player.closeInventory();
            songGUI.openGUI(player);
            return;
        }
    }

    private int getCurrentPageFromTitle(String title) {
        Pattern pattern = Pattern.compile("§6§lQuản lý bài hát §7- Trang (\\d+)/");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) - 1; // Convert to 0-based
        }
        return 0;
    }

    private String getMusicNameFromItemLore(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return null;
        }

        for (String line : item.getItemMeta().getLore()) {
            if (line.startsWith("§7Tên định danh: §e")) {
                return line.substring("§7Tên định danh: §e".length());
            }
        }
        return null;
    }

    private String extractMusicNameFromTitle(String title) {
        if (title.startsWith("§6§lChỉnh sửa: §e")) {
            return title.substring("§6§lChỉnh sửa: §e".length());
        } else if (title.startsWith("§c§lXác nhận xóa: §e")) {
            return title.substring("§c§lXác nhận xóa: §e".length());
        }
        return "";
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Cleanup logic if needed
    }
}






