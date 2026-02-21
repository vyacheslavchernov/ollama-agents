package ru.vych.utils;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Утилитный класс для работы с конфигами
 */
public class ConfigUtils {
    @SneakyThrows
    public static String loadFileContent(String fileName) {
        var absolutePath = ConfigUtils.class.getClassLoader().getResource(fileName);
        if (absolutePath == null) {
            throw new IOException("File not found: " + fileName);
        }
        return Files.readString(Paths.get(absolutePath.toURI()));
    }
}
