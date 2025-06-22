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
                if (args.length != 1)
                    return pSendMessage(sender, config.getString("wrong"));

                if (!datalist.isEmpty()) {

                    ComponentBuilder cb = new ComponentBuilder("✎ " + config.getString("ignore-list") + " ");
                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("click"));
                    String lastStr = datalist.get(datalist.size() - 1);

                    for (String str : datalist) {
                        cb.append(str)
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mignore " + str))
                                .event(hover);
                        if (!str.equals(lastStr))
                            cb.append(", ").reset();

                    } sender.spigot().sendMessage(cb.create());

                    return true;

                } else return pSendMessage(sender, "§e✎§r " + config.getString("ignore-empty"));

            case "player":
                if (args.length != 2)
                    return pSendMessage(sender, config.getString("wrong"));
                Optional<Player> target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[1]));

                if (target.isEmpty())
                    return pSendMessage(sender, config.getString("not-found"));
                Player receiver = target.get();

                if (receiver.getUniqueId() == sender.getUniqueId())
                    return pSendMessage(sender, "§c\uD83D\uDC64§r " + config.getString("yourself"));
                String recName = receiver.getName();

                if (datalist.contains(recName)) {
                    datalist.remove(recName);
                    if (datalist.isEmpty())
                        sender.removeMetadata("litepm.ignore", Main.getPlugin());
                    return pSendMessage(sender, "§e\uD83D\uDC64§r " + recName + " " + Config.getConfig().getString("ignore-remove"));
                } else {
                    datalist.add(recName);
                    sender.setMetadata("litepm.ignore", new FixedMetadataValue(Main.getPlugin(), String.join(",", datalist)));
                    return pSendMessage(sender, "§a\uD83D\uDC64§r " + recName + " " + Config.getConfig().getString("ignore-add"));
                }

            default:
                return pSendMessage(sender, config.getString("wrong"));
        }
    }
}
