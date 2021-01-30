package com.ruverq.rubynex.economics.Economy.BasicCommands;

import com.ruverq.rubynex.economics.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) return true;
        Main.managerBank.reset();
        sender.sendMessage("ГоТовО!");
        return true;
    }
}
