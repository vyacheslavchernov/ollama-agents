package ru.vych.tools;

import ru.vych.agent.tools.ToolJsonResponseWrapper;

import java.io.File;
import java.nio.file.FileSystems;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ru.vych.agent.utils.DateTimeUtils.getFormattedActualTime;

/**
 * Набор методов, которые используются агентами
 * как инструменты для работы с системой
 */
@SuppressWarnings("unused")
public class SystemTools {
    /**
     * Собирает и возвращает сводную информацию о системе на которой работает агент.
     *
     * @return сводная информация о системе
     */
    public static String collectSystemInfo() {
        Locale locale = Locale.getDefault();

        var response = new ToolJsonResponseWrapper("system_info", new HashMap<>())
                .addContent("system", Map.of(
                        "os_name", String.format("%s %s", System.getProperty("os.name"), System.getProperty("os.version")),
                        "user_name", System.getProperty("user.name"),
                        "user_home", System.getProperty("user.home"),
                        "file_system_separator", FileSystems.getDefault().getSeparator(),
                        "file_system_path_separator", File.pathSeparator,
                        "java_version", System.getProperty("java.version")
                ))
                .addContent("datetime", Map.of(
                        "local_datetime", getFormattedActualTime(),
                        "timezone", ZoneId.systemDefault().getId()
                ))
                .addContent("locale", Map.of(
                        "language", locale.getLanguage(),
                        "country", locale.getCountry(),
                        "variant", locale.getVariant()
                ));

        return response.json();
    }
}
