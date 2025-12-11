package org.rynx.regionMusic.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rynx.regionMusic.RegionMusic;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.WorldGuardUtils;

public class ToggleMusicCommand implements CommandExecutor {
    
    private final RegionMusic plugin;
    private final MusicManager musicManager;
    private final MusicToggleManager toggleManager;
    private final RegionConfigManager configManager;
    
    public ToggleMusicCommand(RegionMusic plugin, MusicManager musicManager, 
                              MusicToggleManager toggleManager, RegionConfigManager configManager) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.toggleManager = toggleManager;
        this.configManager = configManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        org.rynx.regionMusic.manager.MessageManager msgManager = plugin.getMessageManager();
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(msgManager.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        toggleManager.toggleMusic(player);
        
        boolean isOff = toggleManager.isMusicToggledOff(player);
        if (isOff) {
            musicManager.stopMusicForPlayer(player);
            // Clear region tracking để tránh tự động phát lại khi di chuyển
            if (plugin.getRegionListener() != null) {
                plugin.getRegionListener().clearPlayerRegion(player);
            }
            sender.sendMessage(msgManager.getMessage("toggle-off"));
        } else {
            sender.sendMessage(msgManager.getMessage("toggle-on"));
            String currentRegion = WorldGuardUtils.getPlayerRegion(player);
            if (currentRegion != null && configManager.hasRegion(currentRegion)) {
                musicManager.playMusicForPlayer(player, currentRegion);
            }
        }
        
        return true;
    }
}

