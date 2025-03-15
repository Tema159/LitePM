package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.*;

import static me.tema159.litepm.Main.*;

public class m implements CommandExecutor {

    private FileConfiguration config;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        config = Config.getConfig();

        if (args.length == 0) {
            msgConsoleOrPlayer(commandSender, config.getString("wrong"));
            return true;
        } Optional<Player> target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[0]));

        if (target.isEmpty()) {
            msgConsoleOrPlayer(commandSender, config.getString("not-found"));
            return true;
        } String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();

        if (message.isEmpty()) {
            msgConsoleOrPlayer(commandSender, config.getString("empty"));
            return true;
        }

        Player receiver = target.get();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "litepm.color");

        if (!(commandSender instanceof Player sender)) {
            Bukkit.getConsoleSender().spigot().sendMessage(createBaseComponent(true, true, key, message, receiver));
            receiver.spigot().sendMessage(createBaseComponent(false, true, key, message, receiver));
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            return true;
        }

        if (receiver.getUniqueId() == sender.getUniqueId()) {
            pSendMessage(sender, "§c\uD83D\uDC64§r " + config.getString("yourself"));
            return true;
        }

        for (Player p : Arrays.asList(sender, receiver))
            if (!p.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                setColor(p, (float) Math.random());
            } else {
                colorRange(p, key);
            }

        if (MetaDataAsList(sender).contains(receiver.getName())) {
            pSendMessage(sender, "§c\uD83D\uDC64§r " + receiver.getName() + " " + config.getString("ignore-already"));
            return true;
        }

        sender.spigot().sendMessage(createBaseComponent(true, false, key, message, receiver, sender));

        if (!MetaDataAsList(receiver).contains(sender.getName())) {
            receiver.spigot().sendMessage(createBaseComponent(false, false, key, message, receiver, sender));
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
        } return true;
    }

    public static void msgConsoleOrPlayer (CommandSender s, String str) {
        if (s instanceof Player sender) {
            pSendMessage(sender, str);
        } else {
            Bukkit.getConsoleSender().sendMessage(str);
        }
    }

    private BaseComponent[] createBaseComponent(boolean firstConvert, boolean isConsole, NamespacedKey key, String message, Player... vars) {
    // firstConvert - Set first name to "Me"?

        String me = config.getString("Me");
        String[] args = Objects.requireNonNull(config.getString("format")).split("%s");
        String var1name = firstConvert ? (isConsole ? "Console" : me) : (isConsole ? "Console" : vars[1].getName());
        String var2name = firstConvert ? vars[0].getName() : me;
        ChatColor var1col = (isConsole ? ChatColor.GREEN : ChatColor.of(new Color(colorRange(vars[1], key))));

        ComponentBuilder cb = new ComponentBuilder(args[0]).color(ChatColor.WHITE);
        if (!isConsole) {
            cb.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + vars[1].getName() + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.getString("click"))));
        }

        return cb
                .append(var1name).color(var1col)
                .append(args[1]).color(ChatColor.WHITE)
                .append(var2name).color(ChatColor.of(new Color(colorRange(vars[0], key))))
                .append(args[2]).color(ChatColor.WHITE)
                .append(" " + message).reset()
                .create();
    }
}
