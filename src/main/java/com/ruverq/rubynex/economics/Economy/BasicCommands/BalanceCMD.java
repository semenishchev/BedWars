package com.ruverq.rubynex.economics.Economy.BasicCommands;

import com.ruverq.rubynex.economics.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Main.preifx + "Your balance: Ruby: " + Main.managerBank.getPlayersValue(sender.getName(), "ruby") + " Silver: " + Main.managerBank.getPlayersValue(sender.getName(), "silver"));
        return true;
    }
}

