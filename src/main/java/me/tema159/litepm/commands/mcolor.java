package me.tema159.litepm.commands;

import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Duration;

import static me.tema159.litepm.Main.*;

public class mcolor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player sender)) {
            Bukkit.getConsoleSender().sendMessage("Player §conly§r command");
            return true;
        }

        if (hasCooldown(sender.getUniqueId())) return true;
        setCooldown(sender.getUniqueId(), Duration.ofSeconds(1));

        short value;
        FileConfiguration config = Config.getConfig();

        if (args.length != 1)
            return pSendMessage(sender, config.getString("wrong"));

        try {
            value = Short.parseShort(args[0]);
            if (!(value >= 0 && value <= 255))
                return pSendMessage(sender, "§c⏹§r " + config.getString("color-range") + " §e0 - 255");
        } catch (NumberFormatException e) {
            return pSendMessage(sender, "§c⏹§r " + config.getString( "color-range") + " §e0 - 255");
        }

        int colorValue = setColor(sender, (float) value / 255.0f);
        sender.spigot().sendMessage(new ComponentBuilder("§a✎§r " + config.getString("color-set"))
                .append(" " + value).color(ChatColor.of(new Color(colorValue))).create());
        return true;
    }
}