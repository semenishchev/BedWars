package me.mrfunny.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class PlayerApi {

    public static boolean isHeadShot(Player victim, Projectile projectile) {

        Location locA = new Location(victim.getWorld(), victim.getEyeLocation().getX() -0.5, victim.getEyeLocation().getY() - 0.5, victim.getEyeLocation().getZ() - 0.5);
        Location locB = new Location(victim.getWorld(), victim.getEyeLocation().getX() +0.5, victim.getEyeLocation().getY() + 0.5, victim.getEyeLocation().getZ() + 0.5);

        for (double i = 0; i < 256; i+=0.8D) {
            System.out.println(projectile.getLocation() + " | " + victim.getLocation());
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

        Vector dirVec = new Vector(x, y, z).normalize();
        return dirVec;
    }
}
