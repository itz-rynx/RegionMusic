package org.rynx.regionMusic;

import org.bukkit.plugin.java.JavaPlugin;
import org.rynx.regionMusic.command.CancelCommand;
import org.rynx.regionMusic.command.RegionMusicCommand;
import org.rynx.regionMusic.command.RegionMusicTabCompleter;
import org.rynx.regionMusic.command.ToggleMusicCommand;
import org.rynx.regionMusic.command.ToggleMusicTabCompleter;
import org.rynx.regionMusic.listener.ChatListener;
import org.rynx.regionMusic.listener.GUIListener;
import org.rynx.regionMusic.listener.PlayerQuitListener;
import org.rynx.regionMusic.listener.RegionJoinListener;
import org.rynx.regionMusic.listener.RegionListener;
import org.rynx.regionMusic.manager.ConfigManager;
import org.rynx.regionMusic.manager.MusicManager;
import org.rynx.regionMusic.manager.MusicToggleManager;
import org.rynx.regionMusic.manager.RegionConfigManager;
import org.rynx.regionMusic.util.CustomLogger;

public final class RegionMusic extends JavaPlugin {
    
    private RegionConfigManager configManager;
    private ConfigManager configManager2;
    private MusicToggleManager toggleManager;
    private MusicManager musicManager;
    private RegionListener regionListener;
    private org.rynx.regionMusic.manager.MessageManager messageManager;
    private CustomLogger customLogger;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        // Initialize CustomLogger với tên Rynx to và đẹp
        customLogger = new CustomLogger(getLogger());
        
        // Initialize managers
        configManager2 = new ConfigManager(this);
        messageManager = new org.rynx.regionMusic.manager.MessageManager(this);
        configManager = new RegionConfigManager(this);
        toggleManager = new MusicToggleManager(this);
        // Load toggle states từ file
        toggleManager.loadToggles();
        musicManager = new MusicManager(this, configManager, toggleManager);
        
        // Initialize ChatListener
        chatListener = new ChatListener(configManager, customLogger);
        
        // Register listeners
        regionListener = new RegionListener(this, musicManager, configManager);
        getServer().getPluginManager().registerEvents(regionListener, this);
        getServer().getPluginManager().registerEvents(
            new RegionJoinListener(this, musicManager, configManager), this);
        getServer().getPluginManager().registerEvents(
            new PlayerQuitListener(this, musicManager, toggleManager), this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        
        // Register commands
        getCommand("regionmusic").setExecutor(
            new RegionMusicCommand(this, musicManager, configManager, chatListener));
        getCommand("regionmusic").setTabCompleter(new RegionMusicTabCompleter());
        
        getCommand("togglemusic").setExecutor(
            new ToggleMusicCommand(this, musicManager, toggleManager, configManager));
        getCommand("togglemusic").setTabCompleter(new ToggleMusicTabCompleter());
        
        // Register cancel command
        getCommand("cancel").setExecutor(new CancelCommand(chatListener));
        
        // Log với CustomLogger
        customLogger.info("§aRegionMusic v" + configManager2.getVersion() + " được phát triển bởi Rynx :>!");
        if (configManager2.isDebug()) {
            customLogger.debug("§7Debug mode đã được bật");
        }
    }

    @Override
    public void onDisable() {
        // Lưu toggle states trước khi shutdown
        if (toggleManager != null) {
            toggleManager.saveToggles();
        }
        // Plugin shutdown logic
        if (customLogger != null) {
            customLogger.info("§cRegionMusic đã tắt!");
        }
    }
    
    public CustomLogger getCustomLogger() {
        return customLogger;
    }
    
    public ConfigManager getConfigManager2() {
        return configManager2;
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
