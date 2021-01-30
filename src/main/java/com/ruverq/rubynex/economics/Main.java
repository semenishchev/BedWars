package com.ruverq.rubynex.economics;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.ruverq.rubynex.economics.Economy.BasicCommands.BalanceCMD;
import com.ruverq.rubynex.economics.Economy.BasicCommands.MoneyCMD;
import com.ruverq.rubynex.economics.Economy.BasicCommands.PayCMD;
import com.ruverq.rubynex.economics.Economy.BasicCommands.ResetCMD;
import com.ruverq.rubynex.economics.Economy.ManagerBank;
import com.ruverq.rubynex.economics.Economy.PlayerJoinEventForUpdate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;
import java.util.UUID;

public final class Main extends JavaPlugin implements PluginMessageListener {

    public static Main main;
    public static ManagerBank managerBank;

    public static String preifx = ChatColor.RED + "| " + ChatColor.WHITE;

    @Override
    public void onEnable() {

        getServer().getMessenger().registerIncomingPluginChannel(this, "coins:in", this);

        setInstance(this);
        createConfig();

        managerBank = new ManagerBank();
        managerBank.setUp();

        // Events
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEventForUpdate(), this);

        // Commands
        Bukkit.getPluginCommand("money").setExecutor(new MoneyCMD());
        Bukkit.getPluginCommand("balance").setExecutor(new BalanceCMD());
        Bukkit.getPluginCommand("pay").setExecutor(new PayCMD());
        Bukkit.getPluginCommand("resetrubydata").setExecutor(new ResetCMD());

    }

    @Override
    public void onDisable() {
        ManagerBank.mySQLThing.closeConnection();
    }

    public static Main getInstance(){
        return main;
    }

    public static void setInstance(Main inst){
        main = inst;
    }

    private File createConfig(){

        File config = new File(getDataFolder() + File.separator + "config.yml");
        if(!config.exists()){
            getLogger().info("Creating a config file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        return config;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("coins:in")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("7wZk8c5J3mgvFgUbK")) {
            // {data-passwd} пароль данных ()
            UUID uuid = UUID.fromString(in.readUTF());
            String currency = in.readUTF(); // валюта
            int howMuch = in.readInt();
            // тут начисляй
            Main.managerBank.addValueToPlayer(Bukkit.getPlayer(uuid).getName(), howMuch, currency);
        }
    }
}
