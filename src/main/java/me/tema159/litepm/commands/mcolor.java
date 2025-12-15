package me.tema159.litepm.commands;

import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Duration;

import static me.tema159.litepm.Main.*;

public class mcolor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player only command");
            return true;
        }
        Player sender = (Player) commandSender;

        if (hasCooldown(sender.getUniqueId())) return true;
        setCooldown(sender.getUniqueId(), Duration.ofSeconds(1));

        int value;
        if (args.length != 1)
            return pSendMessage(sender, Config.getMessage("wrong"));
        String stopEmoji = ChatColor.RED + "⏹ " + ChatColor.RESET;

        try {
            value = Integer.parseInt(args[0]);
            if (!(value >= 0 && value <= 255))
                return pSendMessage(sender, stopEmoji + Config.getMessage("color-range")
                        + ChatColor.YELLOW + " 0 - 255");
        } catch (NumberFormatException e) {
            return pSendMessage(sender, stopEmoji + Config.getMessage( "color-range")
                    + ChatColor.YELLOW + " 0 - 255");
        }

        int colorValue = setColor(sender, (float) value / 255.0f);
        sender.spigot().sendMessage(new ComponentBuilder(ChatColor.GREEN + "✎ " + ChatColor.RESET
                + Config.getMessage("color-set"))
                .append(" " + value).color(ChatColor.of(new Color(colorValue))).create());
        return true;
    }
}