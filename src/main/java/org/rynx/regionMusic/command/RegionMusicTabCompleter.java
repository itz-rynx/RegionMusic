package org.rynx.regionMusic.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegionMusicTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();
            
            List<String> commands = Arrays.asList("reload", "playmusic", "stopmusic", "togglemusic", "nextsong", "about", "gui", "addmusic");
            for (String cmd : commands) {
                if (cmd.toLowerCase().startsWith(input)) {
                    completions.add(cmd);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }
}


