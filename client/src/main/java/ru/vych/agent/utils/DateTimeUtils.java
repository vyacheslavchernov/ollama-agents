package ru.vych.agent.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитный класс для работы с датой и временем
 */
public class DateTimeUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @return текущая дата и время в отформатированном виде
     */
    public static String getFormattedActualTime() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
