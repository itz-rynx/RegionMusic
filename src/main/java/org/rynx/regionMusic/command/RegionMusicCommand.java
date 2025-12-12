package org.rynx.regionMusic.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.gui.RegionMusicGUI;
import org.rynx.regionMusic.listener.ChatListener;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

public class RegionMusicCommand implements CommandExecutor {
    
    private final RegionMusic plugin;
    private final MusicManager musicManager;
    private final RegionConfigManager configManager;
    private final RegionMusicGUI gui;
    private final ChatListener chatListener;
    
    public RegionMusicCommand(RegionMusic plugin, MusicManager musicManager, RegionConfigManager configManager, ChatListener chatListener) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.configManager = configManager;
        this.gui = new RegionMusicGUI(configManager);
        this.chatListener = chatListener;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        org.rynx.regionMusic.manager.MessageManager msgManager = plugin.getMessageManager();
        
        if (args.length == 0) {
            sender.sendMessage(msgManager.getMessage("usage"));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "about":
                if (!sender.hasPermission("regionmusic.about") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                @SuppressWarnings("deprecation")
                org.bukkit.plugin.PluginDescriptionFile desc = plugin.getDescription();
                String author = desc.getAuthors().isEmpty() ? "rynx" : String.join(", ", desc.getAuthors());
                sender.sendMessage("§6========== §eRegionMusic §6==========");
                sender.sendMessage("§7Version: §f" + desc.getVersion());
                sender.sendMessage("§7Author: §f" + author);
                sender.sendMessage("§7API Version: §f" + desc.getAPIVersion());
                sender.sendMessage("§7Description: §fPhát nhạc tự động khi vào/ra khu vực WorldGuard");
                sender.sendMessage("§7Features:");
                sender.sendMessage("§7  - Hỗ trợ nhiều nhạc trong 1 region");
                sender.sendMessage("§7  - Phát tuần tự và tự động loop");
                sender.sendMessage("§7  - Hỗ trợ vanilla và custom sounds");
                sender.sendMessage("§7  - Tối ưu hiệu năng");
                sender.sendMessage("§6================================");
                break;
                
            case "reload":
                if (!sender.hasPermission("regionmusic.reload") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                configManager.reload();
                msgManager.reload();
                sender.sendMessage(msgManager.getMessage("reload-success"));
                break;
                
            case "playmusic":
                if (!sender.hasPermission("regionmusic.playmusic") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player player = (Player) sender;
                String region = WorldGuardUtils.getPlayerRegion(player);
                if (region != null && configManager.hasRegion(region)) {
                    musicManager.playMusicForPlayer(player, region);
                    sender.sendMessage(msgManager.getMessage("playmusic-success", "{region}", region));
                } else {
                    sender.sendMessage(msgManager.getMessage("playmusic-no-region"));
                }
                break;
                
            case "stopmusic":
                if (!sender.hasPermission("regionmusic.stopmusic") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player playerStop = (Player) sender;
                musicManager.stopMusicForPlayer(playerStop);
                // Clear region tracking để tránh tự động phát lại khi di chuyển
                if (plugin.getRegionListener() != null) {
                    plugin.getRegionListener().clearPlayerRegion(playerStop);
                }
                sender.sendMessage(msgManager.getMessage("stopmusic-success"));
                break;
                
            case "togglemusic":
                if (!sender.hasPermission("regionmusic.togglemusic") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player playerToggle = (Player) sender;
                org.rynx.regionMusic.manager.MusicToggleManager toggleManager = 
                    plugin.getMusicToggleManager();
                toggleManager.toggleMusic(playerToggle);
                boolean isOff = toggleManager.isMusicToggledOff(playerToggle);
                if (isOff) {
                    musicManager.stopMusicForPlayer(playerToggle);
                    // Clear region tracking để tránh tự động phát lại khi di chuyển
                    if (plugin.getRegionListener() != null) {
                        plugin.getRegionListener().clearPlayerRegion(playerToggle);
                    }
                    sender.sendMessage(msgManager.getMessage("toggle-off"));
                } else {
                    sender.sendMessage(msgManager.getMessage("toggle-on"));
                    String currentRegion = WorldGuardUtils.getPlayerRegion(playerToggle);
                    if (currentRegion != null && configManager.hasRegion(currentRegion)) {
                        musicManager.playMusicForPlayer(playerToggle, currentRegion);
                    }
                }
                break;
                
            case "nextsong":
                if (!sender.hasPermission("regionmusic.nextsong") && !sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player playerNext = (Player) sender;
                String nextRegion = WorldGuardUtils.getPlayerRegion(playerNext);
                if (nextRegion != null && configManager.hasRegion(nextRegion)) {
                    musicManager.skipToNextSong(playerNext);
                    // Thông báo skip đã được hiển thị trong skipToNextSong
                } else {
                    sender.sendMessage(msgManager.getMessage("nextsong-no-region"));
                }
                break;
                
            case "gui":
                if (!sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player playerGUI = (Player) sender;
                gui.openGUI(playerGUI);
                break;
                
            case "addmusic":
                if (!sender.hasPermission("regionmusic.admin")) {
                    sender.sendMessage(msgManager.getMessage("no-permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /regionmusic addmusic <tên_bài_hát>");
                    return true;
                }
                Player playerAdd = (Player) sender;
                String musicName = args[1];
                if (configManager.hasMusic(musicName)) {
                    sender.sendMessage("§c§l✗ Bài hát §e" + musicName + " §cđã tồn tại!");
                    return true;
                }
                chatListener.startAddingMusic(playerAdd, musicName);
                break;
                
            default:
                sender.sendMessage(msgManager.getMessage("usage"));
                break;
        }
        
        return true;
    }
}


