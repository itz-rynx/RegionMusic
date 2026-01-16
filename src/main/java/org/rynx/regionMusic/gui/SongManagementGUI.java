package org.rynx.regionMusic.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rynx.regionMusic.manager.RegionConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SongManagementGUI {

    private final RegionConfigManager configManager;
    private static final int ITEMS_PER_PAGE = 28;
    private static final int NAVIGATION_SLOT_PREV = 45;
    private static final int NAVIGATION_SLOT_NEXT = 53;
    private static final int NAVIGATION_SLOT_ADD = 49;
    private static final int NAVIGATION_SLOT_CLOSE = 50;

    public SongManagementGUI(RegionConfigManager configManager) {
        this.configManager = configManager;
    }

    public void openGUI(Player player, int page) {
        Set<String> musicNames = configManager.getAllMusicNames();
        List<String> musicList = new ArrayList<>(musicNames);
        int totalPages = (int) Math.ceil((double) musicList.size() / ITEMS_PER_PAGE);

        // Đảm bảo page hợp lệ
        if (page < 0) page = 0;
        if (page >= totalPages && totalPages > 0) page = totalPages - 1;
        if (totalPages == 0) page = 0;

        String title = "§6§lSong Management §7- Page " + (page + 1) + "/" + Math.max(1, totalPages);
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Hiển thị danh sách bài hát
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, musicList.size());

        for (int i = startIndex; i < endIndex; i++) {
            String musicName = musicList.get(i);
            int slot = i - startIndex;

            ItemStack musicItem = createMusicItem(musicName);
            gui.setItem(slot, musicItem);
        }

        // Navigation items
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§e§lPrevious Page");
            prevPage.setItemMeta(prevMeta);
            gui.setItem(NAVIGATION_SLOT_PREV, prevPage);
        }

        if (page < totalPages - 1 && totalPages > 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§e§lNext Page");
            nextPage.setItemMeta(nextMeta);
            gui.setItem(NAVIGATION_SLOT_NEXT, nextPage);
        }

        // Add new song button
        ItemStack addItem = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addItem.getItemMeta();
        addMeta.setDisplayName("§a§l➕ Add New Song");
        List<String> addLore = new ArrayList<>();
        addLore.add("§7Click to add a new song");
        addLore.add("§7Will open the input interface");
        addMeta.setLore(addLore);
        addItem.setItemMeta(addMeta);
        gui.setItem(NAVIGATION_SLOT_ADD, addItem);

        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§l✕ Close");
        closeItem.setItemMeta(closeMeta);
        gui.setItem(NAVIGATION_SLOT_CLOSE, closeItem);

        // Info item
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§6§lInformation");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Click on a song to edit/delete");
        infoLore.add("§7Click the green button to add new song");
        infoLore.add("§7");
        infoLore.add("§7Total songs: §e" + musicList.size());
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        gui.setItem(47, infoItem);

        player.openInventory(gui);
    }

    public void openGUI(Player player) {
        openGUI(player, 0);
    }

    private ItemStack createMusicItem(String musicName) {
        ItemStack item = new ItemStack(Material.MUSIC_DISC_CAT);
        ItemMeta meta = item.getItemMeta();

        String displayName = configManager.getDisplayNameForMusic(musicName);
        meta.setDisplayName("§6§l" + displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§7Identifier: §e" + musicName);
        lore.add("§7Sound: §f" + configManager.getSoundForMusic(musicName));
        lore.add("§7Duration: §f" + configManager.getIntervalForMusic(musicName) + " seconds");
        lore.add("§7Volume: §f" + configManager.getVolumeForMusic(musicName));
        lore.add("§7Pitch: §f" + configManager.getPitchForMusic(musicName));

        boolean isUsed = configManager.isMusicUsedInRegions(musicName);
        if (isUsed) {
            lore.add("§c⚠ Currently used in regions");
            lore.add("§cCannot delete this song");
        } else {
            lore.add("§a✓ Can delete this song");
        }

        lore.add("");
        lore.add("§eLeft-click §7to edit");
        if (!isUsed) {
            lore.add("§cRight-click §7to delete");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public void openEditGUI(Player player, String musicName) {
        Map<String, Object> musicInfo = configManager.getMusicInfo(musicName);
        if (musicInfo.isEmpty()) {
            player.sendMessage("§c§l✗ Song does not exist!");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, "§6§lEdit: §e" + musicName);

        // Current values display
        ItemStack currentItem = new ItemStack(Material.BOOK);
        ItemMeta currentMeta = currentItem.getItemMeta();
        currentMeta.setDisplayName("§6§lCurrent Information");
        List<String> currentLore = new ArrayList<>();
        currentLore.add("§7Sound: §f" + musicInfo.get("sound"));
        currentLore.add("§7Duration: §f" + musicInfo.get("interval") + " seconds");
        currentLore.add("§7Display name: §f" + musicInfo.get("displayName"));
        currentLore.add("§7Volume: §f" + musicInfo.get("volume"));
        currentLore.add("§7Pitch: §f" + musicInfo.get("pitch"));
        currentMeta.setLore(currentLore);
        currentItem.setItemMeta(currentMeta);
        gui.setItem(4, currentItem);

        // Edit instructions
        ItemStack editItem = new ItemStack(Material.PAPER);
        ItemMeta editMeta = editItem.getItemMeta();
        editMeta.setDisplayName("§e§lEdit Instructions");
        List<String> editLore = new ArrayList<>();
        editLore.add("§7To edit the song, type the command:");
        editLore.add("§e/editmusic " + musicName + " <sound|interval|name|volume|pitch>");
        editLore.add("§7");
        editLore.add("§7Example:");
        editLore.add("§7/editmusic " + musicName + " MUSIC_DISC_CAT|185|New Name|1.0|1.0");
        editLore.add("§7");
        editLore.add("§cNote: Only enter values you want to change!");
        editMeta.setLore(editLore);
        editItem.setItemMeta(editMeta);
        gui.setItem(13, editItem);

        // Back button
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§e§l← Back");
        backItem.setItemMeta(backMeta);
        gui.setItem(18, backItem);

        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§l✕ Close");
        closeItem.setItemMeta(closeMeta);
        gui.setItem(26, closeItem);

        player.openInventory(gui);
    }

    public void openDeleteConfirmGUI(Player player, String musicName) {
        Inventory gui = Bukkit.createInventory(null, 27, "§c§lConfirm Delete: §e" + musicName);

        // Warning item
        ItemStack warningItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta warningMeta = warningItem.getItemMeta();
        warningMeta.setDisplayName("§c§l⚠ WARNING");
        List<String> warningLore = new ArrayList<>();
        warningLore.add("§7Are you sure you want to delete");
        warningLore.add("§7the song §e" + musicName + "§7?");
        warningLore.add("§7");
        warningLore.add("§cThis action cannot be undone!");
        warningLore.add("§7");
        String displayName = configManager.getDisplayNameForMusic(musicName);
        warningLore.add("§7Display name: §f" + displayName);
        warningLore.add("§7Sound: §f" + configManager.getSoundForMusic(musicName));
        warningMeta.setLore(warningLore);
        warningItem.setItemMeta(warningMeta);
        gui.setItem(4, warningItem);

        // Confirm delete button
        ItemStack confirmItem = new ItemStack(Material.RED_WOOL);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName("§c§l✅ CONFIRM DELETE");
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§7Click to permanently delete the song");
        confirmMeta.setLore(confirmLore);
        confirmItem.setItemMeta(confirmMeta);
        gui.setItem(12, confirmItem);

        // Cancel button
        ItemStack cancelItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName("§a§l❌ CANCEL");
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add("§7Click to cancel the delete operation");
        cancelMeta.setLore(cancelLore);
        cancelItem.setItemMeta(cancelMeta);
        gui.setItem(14, cancelItem);

        player.openInventory(gui);
    }
}
