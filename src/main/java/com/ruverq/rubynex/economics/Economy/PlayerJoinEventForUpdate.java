package com.ruverq.rubynex.economics.Economy;

import com.ruverq.rubynex.economics.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventForUpdate implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        Main.managerBank.setUPUser(player);
    }
}
