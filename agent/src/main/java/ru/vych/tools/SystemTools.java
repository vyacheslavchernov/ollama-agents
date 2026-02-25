package ru.vych.tools;

import java.io.File;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        StringBuilder info = new StringBuilder();

        // Системные свойства
        info.append("=== Системные свойства ===\n");
        info.append("OS: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append("\n");
        info.append("Пользователь: ").append(System.getProperty("user.name")).append("\n");
        info.append("Домашняя папка: ").append(System.getProperty("user.home")).append("\n");
        info.append("Разделитель файлов: ").append(FileSystems.getDefault().getSeparator()).append("\n");
        info.append("Разделитель путей: ").append(File.pathSeparator).append("\n");
        info.append("Java версия: ").append(System.getProperty("java.version")).append("\n");

        // Текущее время и дата
        info.append("\n=== Текущее время и дата ===\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        info.append("Текущее время: ").append(LocalDateTime.now().format(formatter)).append("\n");
        info.append("Часовой пояс: ").append(ZoneId.systemDefault().getId()).append("\n");

        // Локаль
        info.append("\n=== Локаль ===\n");
        Locale locale = Locale.getDefault();
        info.append("Язык: ").append(locale.getLanguage()).append("\n");
        info.append("Страна: ").append(locale.getCountry()).append("\n");
        info.append("Вариант: ").append(locale.getVariant()).append("\n");

        return info.toString();
    }
}
