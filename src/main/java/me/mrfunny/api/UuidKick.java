package me.mrfunny.api;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UuidKick implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(commandSender.hasPermission("bedwars.admin")){
            if(args.length == 1){
                Player player = Bukkit.getPlayer(UUID.fromString(args[0]));
                if(player == null) {
                    commandSender.sendMessage(ChatColor.RED + "Player offline");
                    return true;
                }
                player.kickPlayer("disconnected");
            }
        }
        return false;
    }
}
