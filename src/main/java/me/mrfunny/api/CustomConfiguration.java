package me.mrfunny.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CustomConfiguration {

    private File file;
    private YamlConfiguration config;

    public CustomConfiguration(String name, JavaPlugin plugin){
        if(!plugin.getDataFolder().exists()){
            if(!plugin.getDataFolder().mkdirs()){
                plugin.getLogger().severe("Data folder " + name);
            }
        }
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        if(!file.exists()){
            try {
                if(!file.createNewFile()){
                    plugin.getLogger().severe("Error while init config file " + name);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Error while init config file with " + name);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration get() {
        return config;
    }
    public File getFile() {
        return file;
    }
    public void reload() { this.config = YamlConfiguration.loadConfiguration(file); }
    public void save() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save config " + file.getName());
        }
    }
}
