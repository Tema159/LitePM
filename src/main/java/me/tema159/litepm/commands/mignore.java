package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import static me.tema159.litepm.Main.*;

public class mignore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player only command");
            return true;
        }
        Player sender = (Player) commandSender;

        if (hasCooldown(sender.getUniqueId())) return true;
        setCooldown(sender.getUniqueId(), Duration.ofSeconds(1));
        ArrayList<String> datalist = MetaDataAsList(sender);

        switch (args[0]) {
            case "list":
                if (args.length != 1)
                    return pSendMessage(sender, Config.getMessage("wrong"));

                if (!datalist.isEmpty()) {

                    ComponentBuilder cb = new ComponentBuilder("✎ " + Config.getMessage("ignore-list") + " ");
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

                } else return pSendMessage(sender, ChatColor.YELLOW + "✎ " + ChatColor.RESET
                        + Config.getMessage("ignore-empty"));

            case "player":
                if (args.length != 2)
                    return pSendMessage(sender, Config.getMessage("wrong"));
                Optional<Player> target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[1]));

                if (!target.isPresent())
                    return pSendMessage(sender, Config.getMessage("not-found"));
                Player receiver = target.get();

                String playerEmoji = "\uD83D\uDC64 " + ChatColor.RESET;
                if (receiver.getUniqueId() == sender.getUniqueId())
                    return pSendMessage(sender, ChatColor.RED + playerEmoji + Config.getMessage("yourself"));
                String recName = receiver.getName();

                if (datalist.contains(recName)) {
                    datalist.remove(recName);
                    if (datalist.isEmpty())
                        sender.removeMetadata("litepm.ignore", Main.getPlugin());
                    return pSendMessage(sender, ChatColor.YELLOW + playerEmoji
                            + recName + " " + Config.getMessage("ignore-remove"));
                } else {
                    datalist.add(recName);
                    sender.setMetadata("litepm.ignore", new FixedMetadataValue(Main.getPlugin(), String.join(",", datalist)));
                    return pSendMessage(sender, ChatColor.GREEN + playerEmoji + recName
                            + " " + Config.getMessage("ignore-add"));
                }

            default:
                return pSendMessage(sender, Config.getMessage("wrong"));
        }
    }
}
