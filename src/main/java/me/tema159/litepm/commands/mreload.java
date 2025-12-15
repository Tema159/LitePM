package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class mreload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        YamlConfiguration configuration;
        Config config;
        JavaPlugin plugin = Main.getPlugin();

        try {
            File cfile = new File(plugin.getDataFolder(), "config.yml");
            configuration = YamlConfiguration.loadConfiguration(cfile);
            config = new Config(configuration);
            config.setupConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("An unexpected error occurred: " + e.getMessage());
            return true;
        }

        String reloaded = ChatColor.GREEN + "Plugin has been reloaded!";
        if (commandSender instanceof Player)
            commandSender.sendMessage(ChatColor.GREEN + "[LitePM] " + reloaded);

        plugin.getLogger().info(reloaded);
        return true;
    }
}
