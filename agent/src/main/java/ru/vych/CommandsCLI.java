package ru.vych;

import java.util.Map;

public class CommandsCLI {
    private static final Map<String, String> COMMANDS_LIST = Map.of(
            "/stop", "Exit application",
            "/clear", "Clear current chat context",
            "/agent switch <agent_name>", "Switch to another agent",
            "/agent current", "Active agent info",
            "/agent reload", "Reload agent config from disk"
    );

    public static String getHelp() {
        var sb = new StringBuilder("List of available CLI commands:\n");
        COMMANDS_LIST.entrySet().forEach((entry) -> {
            sb.append("\t- '");
            sb.append(entry.getKey());
            sb.append("' ");
            sb.append(entry.getValue());
            sb.append("\n");
        });

        return sb.toString();
    }
}
