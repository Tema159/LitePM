package me.tema159.litepm;

import me.tema159.litepm.commands.*;
import me.tema159.litepm.utils.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.awt.Color.HSBtoRGB;
import static me.tema159.litepm.utils.Config.sendmsgtype;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static final Map<UUID, Instant> cd = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        new Config().setupConfig();

        Map<String, CommandExecutor> commandMap = Map.of(
                "m", new m(),
                "mcolor", new mcolor(),
                "mignore", new mignore(),
                "mreload", new mreload()
        );

        TabCompleter tabCompleter = new TabCompleter();
        for (Map.Entry<String, CommandExecutor> entry : commandMap.entrySet()) {
            Objects.requireNonNull(getCommand(entry.getKey())).setExecutor(entry.getValue());
            Objects.requireNonNull(getCommand(entry.getKey())).setTabCompleter(tabCompleter);
        }

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(new LogFilter());
    }

    public static Main getPlugin() { return plugin; }

    public static void setCooldown(UUID key, Duration duration) {
        cd.put(key, Instant.now().plus(duration));
    }

    public static boolean hasCooldown(UUID key) {
        Instant cooldown = cd.get(key);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    public static ArrayList<String> MetaDataAsList(Player p) {
        if (p.hasMetadata("litepm.ignore"))
            return new ArrayList<>(Arrays.asList(p.getMetadata("litepm.ignore").get(0).asString().split(",")));
        return new ArrayList<>();
    }

    public static boolean pSendMessage(Player p, String str) {
        p.spigot().sendMessage(sendmsgtype, new TextComponent(str));
        return true;
    }

    public static Integer colorRange(Player p, NamespacedKey key) {
        Integer colorData = p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (colorData == null || colorData <= 0 || colorData > 0xFFFFFF)
            return setColor(p, (float) Math.random());
        return colorData;
    }

    public static int setColor(Player p, float hue) {

        int argb = HSBtoRGB(hue, 0.4f, 1.0f);
        int colorInteger = argb & 0xFFFFFF;

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "litepm.color");
        p.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, colorInteger);

        return colorInteger;
    }
}
