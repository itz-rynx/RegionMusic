package org.rynx.regionMusic.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegionMusicGUI {
    
    private final RegionConfigManager configManager;
    
    public RegionMusicGUI(RegionConfigManager configManager) {
        this.configManager = configManager;
    }
    
    public void openGUI(Player player) {
        String currentRegion = WorldGuardUtils.getPlayerRegion(player);
        
        // Tạo inventory với size 54 (6 hàng)
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lRegionMusic §7- §e" + 
            (currentRegion != null ? currentRegion : "Không có region"));
        
        // Item hiển thị thông tin region hiện tại
        ItemStack regionInfo = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta regionMeta = regionInfo.getItemMeta();
        regionMeta.setDisplayName("§6§lRegion hiện tại");
        List<String> regionLore = new ArrayList<String>();
        if (currentRegion != null) {
            regionLore.add("§7Tên: §e" + currentRegion);
            if (configManager.hasRegion(currentRegion)) {
                List<String> musicList = configManager.getMusicListForRegion(currentRegion);
                regionLore.add("§7Số bài hát: §e" + musicList.size());
                regionLore.add("§7Chế độ phát: §e" + configManager.getPlayModeForRegion(currentRegion));
                regionLore.add("");
                regionLore.add("§7Danh sách bài hát:");
                for (int i = 0; i < musicList.size(); i++) {
                    String musicName = musicList.get(i);
                    String displayName = configManager.getDisplayNameForMusic(musicName);
                    regionLore.add("§7  " + (i + 1) + ". §e" + displayName);
                }
            } else {
                regionLore.add("§cRegion chưa được cấu hình!");
            }
        } else {
            regionLore.add("§7Bạn không ở trong region nào");
        }
        regionMeta.setLore(regionLore);
        regionInfo.setItemMeta(regionMeta);
        gui.setItem(4, regionInfo);
        
        // Hiển thị tất cả regions được cấu hình
        int slot = 9; // Bắt đầu từ slot 9
        for (Map.Entry<String, List<String>> entry : configManager.getAllRegions().entrySet()) {
            if (slot >= 45) break; // Chỉ hiển thị trong 5 hàng đầu
            
            String regionName = entry.getKey();
            List<String> musicList = entry.getValue();
            
            ItemStack regionItem = new ItemStack(Material.MUSIC_DISC_CAT);
            ItemMeta meta = regionItem.getItemMeta();
            meta.setDisplayName("§6§l" + regionName);
            List<String> lore = new ArrayList<String>();
            lore.add("§7Số bài hát: §e" + musicList.size());
            lore.add("§7Chế độ phát: §e" + configManager.getPlayModeForRegion(regionName));
            lore.add("");
            lore.add("§7Danh sách bài hát:");
            for (int i = 0; i < musicList.size() && i < 5; i++) {
                String musicName = musicList.get(i);
                String displayName = configManager.getDisplayNameForMusic(musicName);
                lore.add("§7  " + (i + 1) + ". §e" + displayName);
            }
            if (musicList.size() > 5) {
                lore.add("§7  ... và " + (musicList.size() - 5) + " bài khác");
            }
            if (regionName.equals(currentRegion)) {
                lore.add("");
                lore.add("§a§l✓ Bạn đang ở đây");
            }
            meta.setLore(lore);
            regionItem.setItemMeta(meta);
            gui.setItem(slot, regionItem);
            slot++;
        }
        
        // Item đóng GUI
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§lĐóng");
        closeItem.setItemMeta(closeMeta);
        gui.setItem(49, closeItem);
        
        player.openInventory(gui);
    }
}






