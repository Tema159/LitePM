package me.tema159.litepm.commands;

import me.tema159.litepm.Main;
import me.tema159.litepm.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

import static me.tema159.litepm.Main.*;

public class m implements CommandExecutor {

    private FileConfiguration config;
    private NamespacedKey key;
    private CommandSender cmdSender;
    private int count;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        config = Main.getPlugin().getConfig();
        if (args.length == 0) {
            msgConsoleOrPlayer(Config.getMessage("wrong"));
            return false;
        }

        cmdSender = commandSender;
        HashSet<Player> targets = parseTargets(args);
        if (targets == null) return false;

        String message = String.join(" ", Arrays.copyOfRange(args, count + 1, args.length)).trim();

        if (message.isEmpty()) {
            msgConsoleOrPlayer(Config.getMessage("empty"));
            return false;
        }

        key = new NamespacedKey(Main.getPlugin(), "litepm.color");

        if (!(commandSender instanceof Player)) {
            Bukkit.getConsoleSender().spigot().sendMessage(
                    createBaseComponent(true, true, message, targets.toArray(new Player[0])));
            for (Player receiver : targets) {
                receiver.spigot().sendMessage(createBaseComponent(false, true, message, receiver));
                receiver.playSound(receiver.getLocation(), Config.sound, 1.0f, 1.0f);
            } return true;
        } else return handlePlayerMessage(targets, message);
    }

    private HashSet<Player> parseTargets(String[] args) {
        HashSet<Player> targets = new HashSet<>();
        count = 0;

        if (args[0].equals("[")) {
            for (String str : args) {
                if (str.equals("]")) break;
                count++;
            }

            if (count == args.length || count < 2) {
                msgConsoleOrPlayer(Config.getMessage("wrong"));
                return null;
            }

            if (count > 11) {
                msgConsoleOrPlayer(ChatColor.RED + "[\uD83D\uDC64 ... ] >≠ 10");
                return null;
            }

            for (int i = 1; i < count; i++) {
                Optional<Player> target = Optional.ofNullable(cmdSender.getServer().getPlayerExact(args[i]));
                if (!target.isPresent()) {
                    msgConsoleOrPlayer(args[i] + " " + Config.getMessage("not-found"));
                    return null;
                } targets.add(target.get());
            }
        } else {
            Optional<Player> target = Optional.ofNullable(cmdSender.getServer().getPlayerExact(args[0]));
            if (!target.isPresent()) {
                msgConsoleOrPlayer(args[0] + " " + Config.getMessage("not-found"));
                return null;
            } targets.add(target.get());
        }
        return targets;
    }

    private boolean handlePlayerMessage(HashSet<Player> targets, String message) {
        Player sender = (Player) cmdSender;
        UUID senderID = sender.getUniqueId();
        ArrayList<String> metadata = MetaDataAsList(sender);

        for (Player receiver : targets) {
            if (receiver.getUniqueId() == senderID)
                return pSendMessage(sender, ChatColor.RED + "\uD83D\uDC64 " + ChatColor.RESET
                        + Config.getMessage("yourself"));
            if (metadata.contains(receiver.getName()))
                return pSendMessage(sender, ChatColor.RED + "\uD83D\uDC64 " + ChatColor.RESET
                        + receiver.getName() + " " + Config.getMessage("ignore-already"));
            if (!receiver.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
                setColor(receiver, (float) Math.random());
            else colorRange(receiver, key);
        }

        if (!sender.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
            setColor(sender, (float) Math.random());
        else colorRange(sender, key);

        sender.spigot().sendMessage(createBaseComponent(true, false, message, Stream.concat(targets.stream(), Stream.of(sender)).toArray(Player[]::new)));
        String senderName = sender.getName();

        for (Player receiver : targets)
            if (!MetaDataAsList(receiver).contains(senderName)) {
                receiver.spigot().sendMessage(createBaseComponent(false, false, message, receiver, sender));
                receiver.playSound(receiver.getLocation(), Config.sound, 1.0f, 1.0f);
            }
        return true;
    }

    private void msgConsoleOrPlayer(String str) {
        if (cmdSender instanceof Player)
            pSendMessage((Player) cmdSender, str);
        else Bukkit.getConsoleSender().sendMessage(str);
    }

    private BaseComponent[] createBaseComponent(boolean firstConvert, boolean isConsole, String message, Player... vars) {
    // firstConvert - Set first name to "Me"?
        String me = Config.getMessage("me");
        Player pSender = vars[vars.length - 1];
        String[] args = Objects.requireNonNull(config.getString("format")).split("%s");

        String firstName = isConsole ? "Console" : (firstConvert ? me : pSender.getName());
        ChatColor var1col = isConsole ? ChatColor.GREEN : ChatColor.of(new Color(colorRange(pSender, key)));

        ComponentBuilder cb = new ComponentBuilder().color(ChatColor.WHITE);

        if (!isConsole) {
            String clickName = buildClickTarget(firstConvert, vars);
            cb.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + clickName + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Config.getMessage("click"))));
        }

        cb.append(args[0])
                .append(firstName).color(var1col)
                .append(args[1]).color(ChatColor.WHITE)
                .append(firstConvert ? vars[0].getName() : me).color(ChatColor.of(new Color(colorRange(vars[0], key))));

        if (isConsole ? (vars.length != 1) : (vars.length > 2))
            cb.append(", ").color(ChatColor.WHITE);

        appendRecipients(cb, vars, isConsole);
        return cb.append(args[2]).color(ChatColor.WHITE).append(" " + message).reset().create();
    }

    private String buildClickTarget(boolean firstConvert, Player[] vars) {
        StringBuilder clickName;
        Player pSender = vars[vars.length - 1];

        if (firstConvert && vars.length > 2) {
            clickName = new StringBuilder("[ ");
            for (int i = 0; i < vars.length - 1; i++)
                clickName.append(vars[i].getName()).append(" ");
            clickName.append("]");
        } else clickName = new StringBuilder(firstConvert ? vars[0].getName() : pSender.getName());

        return clickName.toString();
    }

    private void appendRecipients(ComponentBuilder cb, Player[] vars, boolean isConsole) {
        for (int i = 1; i < vars.length; i++) {
            if (isConsole || i != vars.length - 1)
                cb.append(vars[i].getName()).color(ChatColor.of(new Color(colorRange(vars[i], key))));
            if (i < vars.length - (isConsole ? 1 : 2))
                cb.append(", ").color(ChatColor.WHITE);
        }
    }
}
