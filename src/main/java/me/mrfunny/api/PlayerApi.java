package me.mrfunny.api;

import me.mrfunny.bedwars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

public class PlayerApi {

    public static boolean isHeadShot(Player victim, Projectile projectile) {

        Location locA = new Location(victim.getWorld(), victim.getEyeLocation().getX() -0.5, victim.getEyeLocation().getY() - 0.5, victim.getEyeLocation().getZ() - 0.5);
        Location locB = new Location(victim.getWorld(), victim.getEyeLocation().getX() +0.5, victim.getEyeLocation().getY() + 0.5, victim.getEyeLocation().getZ() + 0.5);

        for (double i = 0; i < 256; i+=0.8D) {
            projectile.getLocation().add(projectile.getVelocity().normalize().multiply(i));
            if (isInCuboid(locA, locB, projectile.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInCuboid(Location min, Location max, Location varying) {
        double[] locs = new double[2];
        locs[0] = min.getX();
        locs[1] = max.getX();
        Arrays.sort(locs);
        if (varying.getX() > locs[1] || varying.getX() < locs[0])
            return false;
        locs[0] = min.getY();
        locs[1] = max.getY();
        Arrays.sort(locs);
        if (varying.getY() > locs[1] || varying.getY() < locs[0])
            return false;
        locs[0] = min.getZ();
        locs[1] = max.getZ();
        Arrays.sort(locs);
        return !(varying.getZ() > locs[1]) && !(varying.getZ() < locs[0]);
    }

    public static Vector getBulletVelocity(Player shooter) {
        double yaw = Math.toRadians((-shooter.getLocation().getYaw() - 90.0f));
        double pitch = Math.toRadians(-shooter.getLocation().getPitch());

        double x = Math.cos(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch);
        double z = -Math.sin(yaw) * Math.cos(pitch);

        return new Vector(x, y, z).normalize();
    }

    public static void clearInventoryExceptArmor(Player player){
        ItemStack[] armorContents = player.getInventory().getArmorContents().clone();
        player.getInventory().clear();
        player.getInventory().setArmorContents(armorContents);
        player.updateInventory();
    }

//    public static void cooldownItem(Player player, ItemStack item) {
//        EntityPlayer cPlayer = ((CraftPlayer)player).getHandle();
//        PlayerConnection playerConnection = cPlayer.playerConnection;
//        PacketPlayOutSetCooldown itemPacket = new PacketPlayOutSetCooldown(CraftItem, 100);
//        playerConnection.sendPacket(itemPacket);
//    }

    public static Player getNearestPlayerFromOtherTeam(Player player, GameManager gameManager){
        double max = gameManager.getWorld().world.getWorldBorder().getSize();
        Player closestPlayer = null;
        try {
            for(Player iterPlayer : Bukkit.getOnlinePlayers()){
                if(GameManager.Companion.isLagged()) continue;
                if(player == iterPlayer) continue;
                if(iterPlayer.getGameMode() == GameMode.SPECTATOR) continue;
                if(iterPlayer.getLocation().getWorld().equals(player.getLocation().getWorld())){
                    double dist = iterPlayer.getLocation().distance(player.getLocation());
                    if (dist < max){
                        max = dist;
                        if(!Objects.equals(gameManager.getWorld().getIslandForPlayer(iterPlayer), gameManager.getWorld().getIslandForPlayer(player))){
                            closestPlayer = iterPlayer;
                        }
                    }
                }
            }
        }catch (Exception ignored){}

        return closestPlayer;
    }

    public void test(Block block){

    }
}
