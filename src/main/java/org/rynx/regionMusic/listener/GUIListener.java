package org.rynx.regionMusic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("§6§lRegionMusic")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            // Đóng GUI khi click vào barrier
            if (event.getCurrentItem().getType().toString().equals("BARRIER")) {
                if (event.getWhoClicked() instanceof Player) {
                    ((Player) event.getWhoClicked()).closeInventory();
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Có thể thêm logic cleanup nếu cần
    }
}

