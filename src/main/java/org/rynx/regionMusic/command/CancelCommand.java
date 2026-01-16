package org.rynx.regionMusic.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rynx.regionMusic.listener.ChatListener;

public class CancelCommand implements CommandExecutor {
    
    private final ChatListener chatListener;
    
    public CancelCommand(ChatListener chatListener) {
        this.chatListener = chatListener;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cChỉ player mới có thể sử dụng lệnh này!");
            return true;
        }
        
        Player player = (Player) sender;
        chatListener.cancelAddingMusic(player);
        return true;
    }
}

