package me.tema159.litepm.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    public static final List<String> defCommands = Arrays.asList("m", "w", "msg", "tell");

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1)
            switch (s) {
                case "mignore": return Arrays.asList("list", "player");
                case "mcolor": return Collections.singletonList("0-255");
                case "m": case "w": case "msg": case "tell": return null;
                default: return new ArrayList<>();
            }
        List<String> message = Collections.singletonList(Config.getMessage("message"));

        if (defCommands.contains(s) && strings.length > 1 && strings[0].equals("[")) {
            if (strings[strings.length - 2].equals("]"))
                return message;
            for (String str : strings)
                if (str.equals("]")) return new ArrayList<>();
            return null;
        }

        if (strings.length == 2) {
            if (s.equals("mignore") && Objects.equals(strings[0], "player"))
                return null;
            if (defCommands.contains(s))
                return message;
        } return new ArrayList<>();
    }
}
