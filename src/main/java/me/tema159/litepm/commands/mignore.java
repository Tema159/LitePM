package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import static me.tema159.litepm.Main.*;

public class mignore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player sender)) {
            Bukkit.getConsoleSender().sendMessage("Player §conly§r command");
            return true;
        }

        if (hasCooldown(sender.getUniqueId())) return true;
        setCooldown(sender.getUniqueId(), Duration.ofSeconds(1));

        FileConfiguration config = Config.getConfig();
        ArrayList<String> datalist = MetaDataAsList(sender);

        switch (args[0]) {
            
            case "list":
                if (args.length != 1) {
                    pSendMessage(sender, config.getString("wrong"));
                    return true;
                }

                if (!datalist.isEmpty()) {
                    ComponentBuilder cb = new ComponentBuilder("✎ " + config.getString("ignore-list") + " ");
                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("click"));

                    for (String str : datalist) {
                        cb.append(str)
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mignore " + str))
                                .event(hover);
                        if (!str.equals(datalist.get(datalist.size() - 1))) {
                            cb.append(", ").reset();
                        }
                    } sender.spigot().sendMessage(cb.create());

                } else {
                    pSendMessage(sender, "§e✎§r " + config.getString("ignore-empty"));
                }

                return true;

            case "player":
                if (args.length != 2) {
                    pSendMessage(sender, config.getString("wrong"));
                    return true;
                } Optional<Player> target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[1]));

                if (target.isEmpty()) {
                    pSendMessage(sender, config.getString("not-found"));
                    return true;
                } Player receiver = target.get();

                if (receiver.getUniqueId() == sender.getUniqueId()) {
                    pSendMessage(sender, "§c\uD83D\uDC64§r " + config.getString("yourself"));
                    return true;
                }

                String recName = receiver.getName();

                if (datalist.contains(recName)) {
                    datalist.remove(recName);
                    pSendMessage(sender, "§e\uD83D\uDC64§r " + recName + " " + Config.getConfig().getString("ignore-remove"));
                    if (datalist.isEmpty()) {
                        sender.removeMetadata("litepm.ignore", Main.getPlugin());
                        return true;
                    }
                } else {
                    datalist.add(recName);
                    pSendMessage(sender, "§a\uD83D\uDC64§r " + recName + " " + Config.getConfig().getString("ignore-add"));
                } sender.setMetadata("litepm.ignore", new FixedMetadataValue(Main.getPlugin(), String.join(",", datalist)));

                return true;

            default:
                pSendMessage(sender, config.getString("wrong"));
                return true;
        }
    }
}
