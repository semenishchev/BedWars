package com.ruverq.rubynex.economics.Economy.BasicCommands;

import com.ruverq.rubynex.economics.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MoneyCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("Economics.money")){
            sender.sendMessage(ChatColor.RED + "/ No permission");
            return true;
        }

        if(args.length < 2){
            sender.sendMessage(ChatColor.RED + "/ Not enough arguments: /money add/remove [number] [nick] silver/ruby");
            return true;
        }
        String what = "silver";
        if(args.length > 3){
            what = args[3];
        }

        if(!what.equalsIgnoreCase("silver") && !what.equalsIgnoreCase("ruby")){
            what = "silver";
        }
        what = what.toLowerCase();

        String toWhom = sender.getName();
        int moneyToExecute = 0;
        if(!StringUtils.isNumeric(args[1])){
            if(args.length < 3){
                sender.sendMessage(ChatColor.RED + "/ Second argument is not a number");
                return true;
            }else if(!StringUtils.isNumeric(args[2])){
                sender.sendMessage(ChatColor.RED + "/ Third argument is not a number");
                return true;
            }
            toWhom = args[1];
            moneyToExecute = Integer.parseInt(args[2]);
        }else{
            moneyToExecute = Integer.parseInt(args[1]);
        }

        String commandtwo = args[0];
        if(commandtwo.equalsIgnoreCase("add")){
            Main.managerBank.addValueToPlayer(toWhom, moneyToExecute, what);
            sender.sendMessage(Main.preifx + ChatColor.GREEN + "Added " + moneyToExecute + " " + what + " to " + toWhom + "'s balance");
        }else if(commandtwo.equalsIgnoreCase("remove")){
            Main.managerBank.removeValueFromPlayer(toWhom, moneyToExecute, what);
            sender.sendMessage(Main.preifx + ChatColor.GREEN + "Removed " + moneyToExecute + " " + what + " from " + toWhom + "'s balance");
        }

        return true;
    }
}
