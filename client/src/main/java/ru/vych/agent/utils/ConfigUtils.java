package ru.vych.agent.utils;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Утилитный класс для работы с конфигами
 */
public class ConfigUtils {
    /**
     * Прочитать содержимое текстового файла по
     * относительному пути в папке ресурсов проекта
     *
     * @param relativePath относительный путь до файла
     * @return содержимое файла
     */
    @SneakyThrows
    public static String readFileContentFromResources(String relativePath) {
        var absolutePath = ConfigUtils.class.getClassLoader().getResource(relativePath);
        if (absolutePath == null) {
            throw new IOException("File not found in resources: " + relativePath);
        }
        return readFileContentFromAbsolutePath(absolutePath.toString().replace("file:/", ""));
    }

    /**
     * Прочитать содержимое текстового файла по
     * абсолютному пути в системе
     *
     * @param absolutePath относительный путь до файла
     * @return содержимое файла
     */
    @SneakyThrows
    public static String readFileContentFromAbsolutePath(String absolutePath) {
        return String.join("\n", Files.readAllLines(Paths.get(absolutePath)));
    }
}
