package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public final class mreload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        YamlConfiguration configuration;
        Config config;
        String prefix = "[LitePM] ";

        try {
            File cfile = new File(Main.getPlugin().getDataFolder(), "config.yml");
            configuration = YamlConfiguration.loadConfiguration(cfile);
            config = new Config(configuration);
            config.setupConfig();
        } catch (Exception e) {
            Bukkit.getLogger().severe(prefix + "An unexpected error occurred: " + e.getMessage());
            return true;
        }

        String reloaded = "§a" + prefix + "Plugin has been reloaded!";

        if (commandSender instanceof Player) {
            Player sender = Bukkit.getPlayer(((Player) commandSender).getUniqueId());
            Objects.requireNonNull(sender).sendMessage(reloaded);
        } Bukkit.getConsoleSender().sendMessage(reloaded);

        return true;
    }
}
