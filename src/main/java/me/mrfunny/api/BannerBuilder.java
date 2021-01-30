package me.mrfunny.api;

import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;

/**
 * Easily create banners
 * @author MrFunny
 */
public class BannerBuilder {
    @Nonnull
    private Banner banner;

    public BannerBuilder(@Nonnull Banner banner){
        this.banner = banner;
    }

    @Nonnull
    public BannerBuilder(@Nonnull Banner banner, @Nonnull DyeColor color){
        this.banner = banner;
        this.banner.setBaseColor(color);
    }

    @Nonnull
    public BannerBuilder setBaseColor(@Nonnull DyeColor color){
        this.banner.setBaseColor(color);
        return this;
    }

    @Nonnull
    public BannerBuilder addPattern(@Nonnull DyeColor color, @Nonnull PatternType type){
        this.banner.addPattern(new Pattern(color, type));
        return this;
    }

    @Nonnull
    public Banner build(){
        return this.banner;
    }

    /**
    @param section accepts section like this:
     SomeShield:
       base-color: LIME
       patterns:
         1:
           color: BLACK
           type: MOJANG
     and returns banner like this: http://www.planetminecraft.com/banner/?b=b1f
    @info
     all colors are enum of DyeColor
     full list here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html
     */
    @Nonnull
    public Banner build(@Nonnull ConfigurationSection section){
        setBaseColor(DyeColor.valueOf(section.getString("base-color")));
        for(String pattern : section.getConfigurationSection("patterns").getKeys(false)){
            addPattern(DyeColor.valueOf(section.getString("patterns." + pattern + ".color")), PatternType.valueOf(section.getString("patterns." + pattern + ".type")));
        }
        return this.banner;
    }
}
