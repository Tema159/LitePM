package me.tema159.litepm.utils;

import me.tema159.litepm.Main;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Config {

    private final FileConfiguration objConfig;

    public static ChatMessageType sendmsgtype;
    public static Sound sound;

    public Config() {
        this.objConfig = Main.getPlugin().getConfig();
    }

    public Config(FileConfiguration yaml) {
        this.objConfig = yaml;
    }

    public void setupConfig() {
        JavaPlugin plugin = Main.getPlugin();
        FileConfiguration config = plugin.getConfig();
        Objects.requireNonNull(config.getDefaults());

        objConfig.addDefaults(config.getDefaults());
        objConfig.options().copyDefaults(true);

        // validate config

        String format = objConfig.getString("format");
        if (format != null) {
            int count = format.split("%s").length - 1;
            if (count != 2) {
                plugin.getLogger().severe("'format' must contain exactly two '%s'!");
                objConfig.set("format", config.getDefaults().get("format"));
            }
        }

        try {
            sound = Sound.valueOf(Objects.requireNonNull(objConfig.getString("sound")).toUpperCase());
        } catch (Exception e) {
            plugin.getLogger().severe("'sound' not found!");
            sound = Sound.valueOf(config.getDefaults().getString("sound"));
        }

        String system = Objects.requireNonNull(objConfig.getString("system")).toUpperCase();
        if (!(system.equals("CHAT") || system.equals("ACTIONBAR"))) {
            objConfig.set("system", "ACTIONBAR");
            sendmsgtype = ChatMessageType.ACTION_BAR;
        } else sendmsgtype = ChatMessageType.CHAT;

        // save config

        for (String key : objConfig.getKeys(true))
            config.set(key, objConfig.get(key));
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static String getMessage(String path) {
        String raw = Main.getPlugin().getConfig().getString("messages." + path);
        return raw == null ? null : ChatColor.translateAlternateColorCodes('&', raw);
    }
}
