package org.rynx.regionMusic.util;

import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger {
    
    private final Logger logger;
    private final String prefix;
    
    public CustomLogger(Logger logger) {
        this.logger = logger;
        // Tạo prefix với tên Rynx to và đẹp
        this.prefix = ChatColor.GOLD + "[" + ChatColor.BOLD + ChatColor.YELLOW + "R" + 
                     ChatColor.RED + "Y" + ChatColor.GOLD + "N" + ChatColor.YELLOW + "X" + 
                     ChatColor.RESET + ChatColor.GOLD + "] " + ChatColor.RESET;
    }
    
    public void info(String message) {
        logger.log(Level.INFO, prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public void warning(String message) {
        logger.log(Level.WARNING, prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public void severe(String message) {
        logger.log(Level.SEVERE, prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public void debug(String message) {
        logger.log(Level.INFO, prefix + ChatColor.GRAY + "[DEBUG] " + ChatColor.RESET + 
                   ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public void log(Level level, String message) {
        logger.log(level, prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
}






