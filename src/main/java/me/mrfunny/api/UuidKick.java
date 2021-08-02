package me.mrfunny.api;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UuidKick implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(commandSender.hasPermission("bedwars.admin")){
            if(args.length == 1){
                Bukkit.getPlayer(args[0]).kickPlayer("disconnected");
            }
        }
        return false;
    }
}
