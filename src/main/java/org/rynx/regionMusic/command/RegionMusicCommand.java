package org.rynx.regionMusic.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

public class RegionMusicCommand implements CommandExecutor {
    
    private final RegionMusic plugin;
    private final MusicManager musicManager;
    private final RegionConfigManager configManager;
    
    public RegionMusicCommand(RegionMusic plugin, MusicManager musicManager, RegionConfigManager configManager) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.configManager = configManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        org.rynx.regionMusic.manager.MessageManager msgManager = plugin.getMessageManager();
        
        if (!sender.hasPermission("regionmusic.admin")) {
            sender.sendMessage(msgManager.getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(msgManager.getMessage("usage"));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "about":
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
                configManager.reload();
                msgManager.reload();
                sender.sendMessage(msgManager.getMessage("reload-success"));
                break;
                
            case "playmusic":
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
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgManager.getMessage("player-only"));
                    return true;
                }
                Player playerNext = (Player) sender;
                String nextRegion = WorldGuardUtils.getPlayerRegion(playerNext);
                if (nextRegion != null && configManager.hasRegion(nextRegion)) {
                    musicManager.skipToNextSong(playerNext);
                    sender.sendMessage(msgManager.getMessage("nextsong-success"));
                } else {
                    sender.sendMessage(msgManager.getMessage("nextsong-no-region"));
                }
                break;
                
            default:
                sender.sendMessage(msgManager.getMessage("usage"));
                break;
        }
        
        return true;
    }
}


