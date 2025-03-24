package me.tema159.litepm.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 1) {
            return switch (s) {
                case "mignore" -> List.of("list", "player");
                case "mcolor" -> List.of("0-255");
                case "m" -> null;
                default -> new ArrayList<>();
            };
        }

        if (s.equals("m") && strings.length > 1 && strings[0].equals("[")) {
            if (strings[strings.length - 2].equals("]"))
                return List.of(Objects.requireNonNull(Config.getConfig().getString("message")));
            for (String str : strings)
                if (str.equals("]")) return new ArrayList<>();
            return null;
        }

        if (strings.length == 2) {
            if (s.equals("mignore") && Objects.equals(strings[0], "player"))
                return null;
            if (s.equals("m"))
                return List.of(Objects.requireNonNull(Config.getConfig().getString("message")));
        } return new ArrayList<>();
    }
}
