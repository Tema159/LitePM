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
import java.util.stream.Stream;

import static me.tema159.litepm.Main.*;

public class m implements CommandExecutor {

    private FileConfiguration config;
    private static Sound sound;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        config = Config.getConfig();

        if (args.length == 0)
            return msgConsoleOrPlayer(commandSender, config.getString("wrong"));

        HashSet<Player> targets = new HashSet<>();
        Optional<Player> target;
        byte count = 0;

        if (args[0].equals("[")) {

            for (String str : args) {
                if (str.equals("]")) break;
                count++;
            }

            if (count == args.length || count < 2)
                return msgConsoleOrPlayer(commandSender, config.getString("wrong"));

            if (count > 11)
                return msgConsoleOrPlayer(commandSender, "§c[\uD83D\uDC64 ... ] >≠ 10");

            for (int i = 1; i < count; i++) {
                target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[i]));
                if (target.isEmpty())
                    return msgConsoleOrPlayer(commandSender, args[i] + " " + config.getString("not-found"));
                targets.add(target.get());
            }

        } else {
            target = Optional.ofNullable(commandSender.getServer().getPlayerExact(args[0]));
            if (target.isEmpty())
                return msgConsoleOrPlayer(commandSender, args[0] + " " + config.getString("not-found"));
            targets.add(target.get());
        }

        String message = String.join(" ", Arrays.copyOfRange(args, count + 1, args.length)).trim();

        if (message.isEmpty())
            return msgConsoleOrPlayer(commandSender, config.getString("empty"));

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "litepm.color");

        if (!(commandSender instanceof Player sender)) {
            Bukkit.getConsoleSender().spigot().sendMessage(createBaseComponent(true, true, key, message, targets.toArray(new Player[0])));
            for (Player receiver : targets) {
                receiver.spigot().sendMessage(createBaseComponent(false, true, key, message, receiver));
                receiver.playSound(receiver.getLocation(), sound, 1.0f, 1.0f);
            } return true;
        }

        UUID senderID = sender.getUniqueId();
        ArrayList<String> metadata = MetaDataAsList(sender);

        for (Player receiver : targets) {
            if (receiver.getUniqueId() == senderID)
                return pSendMessage(sender, "§c\uD83D\uDC64§r " + config.getString("yourself"));
            if (metadata.contains(receiver.getName()))
                return pSendMessage(sender, "§c\uD83D\uDC64§r " + receiver.getName() + " " + config.getString("ignore-already"));
            if (!receiver.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
                setColor(receiver, (float) Math.random());
            else colorRange(receiver, key);
        }

        if (!sender.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
            setColor(sender, (float) Math.random());
        else colorRange(sender, key);

        sender.spigot().sendMessage(createBaseComponent(true, false, key, message, Stream.concat(targets.stream(), Stream.of(sender)).toArray(Player[]::new)));
        String senderName = sender.getName();

        for (Player receiver : targets)
            if (!MetaDataAsList(receiver).contains(senderName)) {
                receiver.spigot().sendMessage(createBaseComponent(false, false, key, message, receiver, sender));
                receiver.playSound(receiver.getLocation(), sound, 1.0f, 1.0f);
            } return true;
    }

    public static boolean msgConsoleOrPlayer (CommandSender s, String str) {
        if (s instanceof Player sender)
            return pSendMessage(sender, str);
        else Bukkit.getConsoleSender().sendMessage(str);
        return true;
    }

    public static void setSound(Sound snd) { sound = snd; }

    private BaseComponent[] createBaseComponent(boolean firstConvert, boolean isConsole, NamespacedKey key, String message, Player... vars) {
    // firstConvert - Set first name to "Me"?

        String me = config.getString("Me");
        Player pSender = vars[vars.length - 1];
        String[] args = Objects.requireNonNull(config.getString("format")).split("%s");

        String firstName = isConsole ? "Console" : (firstConvert ? me : pSender.getName());
        ChatColor var1col = isConsole ? ChatColor.GREEN : ChatColor.of(new Color(colorRange(pSender, key)));

        ComponentBuilder cb = new ComponentBuilder().color(ChatColor.WHITE);

        if (!isConsole) {
            StringBuilder clickName;

            if (firstConvert && vars.length > 2) {
                clickName = new StringBuilder("[ ");
                for (int i = 0; i < vars.length - 1; i++)
                    clickName.append(vars[i].getName()).append(" ");
                clickName.append("]");
            } else clickName = new StringBuilder(firstConvert ? vars[0].getName() : pSender.getName());

            cb.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + clickName + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(config.getString("click"))));
        }

        cb.append(args[0])
                .append(firstName).color(var1col)
                .append(args[1]).color(ChatColor.WHITE)
                .append(firstConvert ? vars[0].getName() : me).color(ChatColor.of(new Color(colorRange(vars[0], key))));

        if (isConsole ? (vars.length != 1) : (vars.length > 2))
            cb.append(", ").color(ChatColor.WHITE);

        for (int i = 1; i < vars.length; i++) {
            if (isConsole || i != vars.length - 1)
                cb.append(vars[i].getName()).color(ChatColor.of(new Color(colorRange(vars[i], key))));
            if (i < vars.length - (isConsole ? 1 : 2))
                cb.append(", ").color(ChatColor.WHITE);
        } return cb.append(args[2]).color(ChatColor.WHITE).append(" " + message).reset().create();
    }
}
