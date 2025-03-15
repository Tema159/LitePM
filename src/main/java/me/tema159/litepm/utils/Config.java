package me.tema159.litepm.utils;

import me.tema159.litepm.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Config {

    private static FileConfiguration config;
    private final FileConfiguration objConfig;
    private static final JavaPlugin plugin = Main.getPlugin();

    public Config() {
        this.objConfig = plugin.getConfig();
    }

    public Config(FileConfiguration yaml) {
        this.objConfig = yaml;
    }

    public void setupConfig() {
        Map<String, String> defaultSettings = Map.ofEntries(
                Map.entry("Me", "Me"),
                Map.entry("click", "Click to send him a private message"),
                Map.entry("color-range", "&cIncorrect&r color specified! Range:"),
                Map.entry("color-set", "Color &ahas been set&r:"),
                Map.entry("empty", "Message &cis empty"),
                Map.entry("format", "[%s → %s]"),
                Map.entry("ignore-add", "&aadded&r to blacklist"),
                Map.entry("ignore-already", "is &cblacklisted"),
                Map.entry("ignore-empty", "The blacklist is currently &eempty"),
                Map.entry("ignore-list", "Blacklist:"),
                Map.entry("ignore-remove", "&eremoved&r from blacklist"),
                Map.entry("message", "message"),
                Map.entry("not-found", "Player &cnot found"),
                Map.entry("system", "ACTIONBAR"),
                Map.entry("wrong", "&cIncorrect&r arguments entered"),
                Map.entry("yourself", "You &ccannot&r specify &cyourself!")
        );

        for (Map.Entry<String, String> entry : defaultSettings.entrySet()) {
            objConfig.addDefault(entry.getKey(), entry.getValue());
        }

        objConfig.options().header("""
                system: Type of sending system messages (ACTIONBAR, CHAT)
                format: Message sending format, '%s' must be specified twice""");

        objConfig.options().copyDefaults(true);
        validateConfig();
    }

    private void validateConfig() {

        String format = objConfig.getString("format");
        if (!(format == null)) {
            long count = format.split("%s").length - 1;
            if (count != 2) {
                throw new IllegalArgumentException("'format' must contain exactly two '%s'!");
            }
        }

        String system = Objects.requireNonNull(objConfig.getString("system")).toUpperCase();
        if (!Arrays.asList("CHAT", "ACTIONBAR").contains(system)) {
            objConfig.set("system", "ACTIONBAR");
            Bukkit.getLogger().severe("[LitePM] Config error: " +
                    "'system' should be CHAT or ACTIONBAR! Switched to ACTIONBAR");
        }
        saveReload();
    }

    private void saveReload() {

        config = new YamlConfiguration();

        for (String key : objConfig.getKeys(true)) {
            config.set(key, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(objConfig.getString(key))));
            plugin.getConfig().set(key, objConfig.getString(key));
        }

        plugin.saveConfig();
        plugin.reloadConfig();

        if (Objects.equals(Objects.requireNonNull(config.getString("system")).toUpperCase(), "CHAT")) {
            Main.setSendmsgtype(ChatMessageType.CHAT);
        } else {
            Main.setSendmsgtype(ChatMessageType.ACTION_BAR);
        }
    }
    public static FileConfiguration getConfig() {
        return config;
    }
}
