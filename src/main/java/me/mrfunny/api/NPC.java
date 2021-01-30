package me.mrfunny.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.mrfunny.plugins.paper.worlds.Island;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class NPC {

    private MinecraftServer nmsServer;
    private WorldServer nmsWorld;
    private GameProfile gameProfile;
    private EntityPlayer npcEntity;
    private Player npcPlayer;
    public boolean isTouched = false;
    public UUID uuid = UUID.randomUUID();
    public Player player;
    public Island island;

    public NPC(Player player, Island island) {
        this.player = player;
        this.island = island;
        this.nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        this.nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
        this.gameProfile = new GameProfile(uuid, player.getName());
        this.npcEntity = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        npcEntity.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        this.npcPlayer = npcEntity.getBukkitEntity().getPlayer();
        if (npcPlayer == null) return;

        npcPlayer.setPlayerListName("");

        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
            ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npcEntity));
            ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
        }
    }

    public void despawn() {
        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) loopPlayer).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npcEntity.getId()));
        }
    }

    public Player getNpcPlayer() {
        return npcPlayer;
    }

    public EntityPlayer getNpcEntity() {
        return npcEntity;
    }

    public static void setSkin(GameProfile profile, UUID uuid) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false").openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                String reply = sb.toString();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(reply);
                JSONArray properties = (JSONArray) json.get("properties");

                for (Object o : properties) {
                    JSONObject property = (JSONObject) o;
                    String value = (String) property.get("value");
                    String signature = (String) property.get("signature");
                    profile.getProperties().put("textures", new Property("textures", value, signature));
                    break;
                }

            } else {
                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}