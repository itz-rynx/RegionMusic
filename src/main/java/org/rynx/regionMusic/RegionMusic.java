package org.rynx.regionMusic;

import org.bukkit.plugin.java.JavaPlugin;
import org.rynx.regionMusic.command.RegionMusicCommand;
import org.rynx.regionMusic.command.RegionMusicTabCompleter;
import org.rynx.regionMusic.command.ToggleMusicCommand;
import org.rynx.regionMusic.command.ToggleMusicTabCompleter;
import org.rynx.regionMusic.listener.PlayerQuitListener;
import org.rynx.regionMusic.listener.RegionJoinListener;
import org.rynx.regionMusic.listener.RegionListener;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;
import org.rynx.regionMusic.manager.RegionConfigManager;

public final class RegionMusic extends JavaPlugin {
    
    private RegionConfigManager configManager;
    private MusicToggleManager toggleManager;
    private MusicManager musicManager;
    private RegionListener regionListener;
    private org.rynx.regionMusic.manager.MessageManager messageManager;

    @Override
    public void onEnable() {
        // Initialize managers
        messageManager = new org.rynx.regionMusic.manager.MessageManager(this);
        configManager = new RegionConfigManager(this);
        toggleManager = new MusicToggleManager();
        musicManager = new MusicManager(this, configManager, toggleManager);
        
        // Register listeners
        regionListener = new RegionListener(this, musicManager, configManager);
        getServer().getPluginManager().registerEvents(regionListener, this);
        getServer().getPluginManager().registerEvents(
            new RegionJoinListener(this, musicManager, configManager), this);
        getServer().getPluginManager().registerEvents(
            new PlayerQuitListener(this, musicManager, toggleManager), this);
        
        // Register commands
        getCommand("regionmusic").setExecutor(
            new RegionMusicCommand(this, musicManager, configManager));
        getCommand("regionmusic").setTabCompleter(new RegionMusicTabCompleter());
        
        getCommand("togglemusic").setExecutor(
            new ToggleMusicCommand(this, musicManager, toggleManager, configManager));
        getCommand("togglemusic").setTabCompleter(new ToggleMusicTabCompleter());
        
        getLogger().info("RegionMusic được phát triển bởi Rynx :>!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("RegionMusic được phát triển bởi Rynx :>!");
    }
    
    public MusicToggleManager getMusicToggleManager() {
        return toggleManager;
    }
    
    public RegionListener getRegionListener() {
        return regionListener;
    }
    
    public org.rynx.regionMusic.manager.MessageManager getMessageManager() {
        return messageManager;
    }
}
