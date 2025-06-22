package me.tema159.litepm.utils;

import me.tema159.litepm.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;

public class Config {

    private static FileConfiguration config;
    private final FileConfiguration objConfig;
    private static final JavaPlugin plugin = Main.getPlugin();

    public static ChatMessageType sendmsgtype;
    public static Sound sound;

    public Config() {
        this.objConfig = plugin.getConfig();
    }

    public Config(FileConfiguration yaml) {
        this.objConfig = yaml;
    }

    public void setupConfig() {
        final Map<String, String> defaultSettings = Map.ofEntries(
                Map.entry("general.sound", "BLOCK_NOTE_BLOCK_BELL"),
                Map.entry("general.system", "ACTIONBAR"),
                Map.entry("general.format", "[%s → %s]"),
                Map.entry("messages.me", "Me"),
                Map.entry("messages.click", "Click to send a private message"),
                Map.entry("messages.color-range", "&cIncorrect&r color specified! Range:"),
                Map.entry("messages.color-set", "Color &ahas been set&r:"),
                Map.entry("messages.empty", "Message &cis empty"),
                Map.entry("messages.ignore-add", "&aadded&r to blacklist"),
                Map.entry("messages.ignore-already", "is &cblacklisted"),
                Map.entry("messages.ignore-empty", "The blacklist is currently &eempty"),
                Map.entry("messages.ignore-list", "Blacklist:"),
                Map.entry("messages.ignore-remove", "&eremoved&r from blacklist"),
                Map.entry("messages.message", "message"),
                Map.entry("messages.not-found", "&cnot found"),
                Map.entry("messages.wrong", "&cIncorrect&r arguments entered"),
                Map.entry("messages.yourself", "You &ccannot&r specify &cyourself!")
        );

        objConfig.options().header("""
                system: Type of sending system messages (ACTIONBAR, CHAT)
                format: Message sending format, '%s' must be specified twice
                sound: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html""");

        for (Map.Entry<String, String> entry : defaultSettings.entrySet())
            objConfig.addDefault(entry.getKey(), entry.getValue());

        objConfig.options().copyDefaults(true);

        // validate config

        String format = objConfig.getString("general.format");
        if (format != null) {
            int count = format.split("%s").length - 1;
            if (count != 2) {
                plugin.getLogger().severe("'format' must contain exactly two '%s'!");
                objConfig.set("general.format", defaultSettings.get("general.format"));
            }
        }

        try {
            sound = Sound.valueOf(Objects.requireNonNull(objConfig.getString("general.sound")).toUpperCase());
        } catch (Exception e) {
            plugin.getLogger().severe("'sound' not found!");
            sound = Sound.valueOf(defaultSettings.get("general.sound"));
        }

        String system = Objects.requireNonNull(objConfig.getString("general.system")).toUpperCase();
        if (!(system.equals("CHAT") || system.equals("ACTIONBAR"))) {
            objConfig.set("general.system", "ACTIONBAR");
            sendmsgtype = ChatMessageType.ACTION_BAR;
        } else sendmsgtype = ChatMessageType.CHAT;

        // save config

        config = new YamlConfiguration();

        for (String key : objConfig.getKeys(true)) {
            String objKey = Objects.requireNonNull(objConfig.getString(key));
            config.set(key.substring(key.indexOf('.') + 1), ChatColor.translateAlternateColorCodes('&', objKey));
            plugin.getConfig().set(key, objKey);
        }

        plugin.saveConfig();
        plugin.reloadConfig();

    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
