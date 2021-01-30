package com.ruverq.rubynex.economics.Economy.BasicCommands;

import com.ruverq.rubynex.economics.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            sender.sendMessage(ChatColor.RED + "/ Not enough arguments: /pay [nickname] [amount]");
            return true;
        }
        String nickname = args[0];
        String amountstring = args[1];

        if(!StringUtils.isNumeric(amountstring)){
            sender.sendMessage(ChatColor.RED + "/ Amount is not numeric");
            return true;
        }
        int amount = Integer.parseInt(amountstring);

        int currentSilver = Main.managerBank.getPlayersValue(sender.getName(), "ruby");
        if(amount > currentSilver){
            sender.sendMessage(ChatColor.RED + "/ As long as you have enough rubys");
            return true;
        }

        if(nickname.equalsIgnoreCase(sender.getName())){
            sender.sendMessage(ChatColor.RED + "/ You can not pay yourself");
            return true;
        }

        boolean happend = Main.managerBank.addValueToPlayer(nickname, amount, "ruby");
        if(!happend){
            sender.sendMessage(ChatColor.RED + "/ Such a person has never logged into the server");
            return true;
        }
        Main.managerBank.removeValueFromPlayer(sender.getName(), amount, "ruby");

        sender.sendMessage(Main.preifx + "You gave " + amount + " rub to " + nickname);

        Player player = Bukkit.getPlayer(nickname);
        if(player != null && player.isOnline()){
            player.sendMessage(Main.preifx + sender.getName() + " gave you " + amount + " rub");
        }
        return true;
    }
}
