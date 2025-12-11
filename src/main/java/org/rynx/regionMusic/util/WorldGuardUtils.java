package org.rynx.regionMusic.util;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.entity.Player;

public class WorldGuardUtils {
    
    public static boolean isPlayerInRegion(Player player, String regionName) {
        try {
            WorldGuardPlugin wgPlugin = WorldGuardPlugin.inst();
            com.sk89q.worldguard.LocalPlayer localPlayer = wgPlugin.wrapPlayer(player);
            ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()
                    .getApplicableRegions(localPlayer.getLocation());
            
            return regions.getRegions().stream()
                    .anyMatch(region -> region.getId().equalsIgnoreCase(regionName));
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getPlayerRegion(Player player) {
        try {
            WorldGuardPlugin wgPlugin = WorldGuardPlugin.inst();
            com.sk89q.worldguard.LocalPlayer localPlayer = wgPlugin.wrapPlayer(player);
            ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()
                    .getApplicableRegions(localPlayer.getLocation());
            
            return regions.getRegions().stream()
                    .map(region -> region.getId())
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

