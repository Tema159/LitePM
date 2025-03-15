package me.tema159.litepm.utils;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class LogFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {

        String msg = event.getMessage().getFormattedMessage();

        if (msg != null && msg.contains("issued server command: ")) {

            String command = msg.replaceFirst("^.*?issued server command: ", "").trim();
            String[] fixedArray = {"/m ", "/msg ", "/w ", "/tell "};

            for (String prefix : fixedArray) {
                if (command.startsWith(prefix)) {
                    return Result.DENY;
                }
            }
        } return Result.NEUTRAL;
    }
}