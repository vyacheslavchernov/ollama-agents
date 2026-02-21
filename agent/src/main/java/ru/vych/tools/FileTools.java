package ru.vych.tools;

import ru.vych.tools.entities.FileSearchResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Набор методов, которые используются агентами
 * как инструменты для работы с файловой системой
 */
@SuppressWarnings("unused")
public class FileTools {

    /**
     * Перезаписывает содержимое файла новым содержимым.
     *
     * @param filePath   Путь к файлу
     * @param newContent Новое содержимое файла (список строк)
     * @return true, если операция выполнена успешно, иначе false
     */
    public static String overwriteFile(String filePath, String newContent) {
        System.out.printf("=== TOOL CALL ===\nПерезапись файла `%s`. Будет перезаписано %s байт.\n", filePath, newContent.getBytes().length);

        try {
            Files.write(Paths.get(filePath), newContent.getBytes());
            System.out.println("Успешно перезаписано\n\n\n");
            return "{status: Файл " + filePath + " успешно перезаписан}";
        } catch (IOException e) {
            System.out.println("Ошибка при перезаписи файла: " + e.getMessage() + "\n\n\n");
            return "{status: Ошибка при перезаписи: " + e.getMessage() + "}";
        }
    }

    /**
     * Ищет файлы по регулярному выражению в заданном каталоге
     *
     * @param directoryPath путь к каталогу
     * @param pattern       регулярное выражение для поиска
     * @param recursive     флаг рекурсивного поиска
     * @return JSON-строка формата {"founded": [...], "error": "..."}
     */
    public static String findFilesWithPattern(String directoryPath, String pattern, Boolean recursive) {
        System.out.printf("=== TOOL CALL ===\nПоиск в каталоге `%s` по паттерну `%s`, recursive = %s\n", directoryPath, pattern, recursive);
        List<FileSearchResult> searchResults = search(directoryPath, pattern, recursive);

        List<String> foundedPaths = searchResults.stream()
                .map(FileSearchResult::getFilePath)
                .filter(Objects::nonNull)
                .toList();

        List<String> searchErrors = searchResults.stream()
                .map(FileSearchResult::getOccurredErrorMessage)
                .filter(Objects::nonNull)
                .toList();

        String foundedJson = "[" + String.join(", ", foundedPaths) + "]";
        String errorsJson = "[" + String.join(", ", searchErrors) + "]";

        String finalJson = "{\"founded\":" + foundedJson + ", \"error\":" + errorsJson + "}";
        System.out.println("Результаты поиска:\n" + finalJson + "\n\n\n");

        return finalJson;
    }

    /**
     * Открывает текстовый файл по заданному пути, полностью считывает его содержимое в строку
     * и возвращает эту строку.
     *
     * @param filePath путь к файлу
     * @return строка с содержимым файла, или null в случае ошибки
     */
    public static String readTextFileToString(String filePath) {
        System.out.printf("=== TOOL CALL ===\nЧтение файла %s\n\n\n", filePath);
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            return "{\"file\": \"" + filePath + "\", \"content\": " + String.join("\n", lines) + "}";
        } catch (IOException e) {
            return "{\"error\": Ошибка при чтении файла. Текст ошибки: " + e.getMessage() + "}";
        }
    }

    /**
     * Проверяет наличие файла. Если файла нет, создает его и записывает контент.
     *
     * @param filePath Путь к файлу
     * @param content  Список строк для записи в файл
     * @return true, если операция выполнена успешно, иначе false
     */
    public static String createFileIfNotExists(String filePath, String content) {
        System.out.printf(
                "=== TOOL CALL ===\nСоздание нового файла `%s`. Будет перезаписано %s байт.\n\n\n",
                filePath, content.getBytes().length
        );

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.write(path, content.getBytes());
                return "{status: Файл " + filePath + " успешно создан}";
            }
            return "{status: Файл " + filePath + " уже существует. Запись не произведена}";
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла: " + e.getMessage());
            return "{\"error\": Ошибка при создании файла. Текст ошибки: " + e.getMessage() + "}";
        }
    }

    /**
     * Проверяет существование каталога. Если каталог не существует, создает его.
     *
     * @param directoryPath Путь к каталогу
     * @return JSON-строка со статусом или ошибкой
     */
    public static String createDirectoryIfNotExists(String directoryPath) {
        System.out.printf(
                "=== TOOL CALL ===\nСоздание каталога `%s`\n",
                directoryPath
        );

        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
                System.out.printf("Каталог `%s` успешно создан.\n\n\n", directoryPath);
                return "{status: Каталог " + directoryPath + " успешно создан}";
            } else {
                System.out.printf("Каталог `%s` уже существует.\n\n\n", directoryPath);
                return "{status: Каталог " + directoryPath + " уже существует}";
            }

        } catch (IOException e) {
            System.err.println("Ошибка при создании каталога: " + e.getMessage() + "\n\n\n");
            return "{\"error\": Ошибка при создании каталога. Текст ошибки: " + e.getMessage() + "}";
        }
    }

    private static List<FileSearchResult> search(String directoryPath, String pattern, boolean recursive) {
        try {
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                throw new IllegalArgumentException("Указанный путь не является существующим каталогом: " + directoryPath);
            }

            Pattern regexPattern = Pattern.compile(pattern);

            List<FileSearchResult> foundFiles = new ArrayList<>();

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)) {
                        if (matchesPattern(entry.getFileName().toString(), regexPattern)) {
                            foundFiles.add(new FileSearchResult(entry.toAbsolutePath().toString(), null));
                        }
                        if (!recursive) continue;
                    }

                    if (matchesPattern(entry.getFileName().toString(), regexPattern)) {
                        foundFiles.add(new FileSearchResult(entry.toAbsolutePath().toString(), null));
                    }
                }
            }

            if (recursive) {
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                    for (Path entry : directoryStream) {
                        if (Files.isDirectory(entry)) {
                            foundFiles.addAll(search(entry.toAbsolutePath().toString(), pattern, true));
                        }
                    }
                } catch (Exception e) {
                    return List.of(new FileSearchResult(null, e.getMessage()));
                }
            }

            return foundFiles;

        } catch (Exception e) {
            return List.of(new FileSearchResult(null, e.getMessage()));
        }
    }

    private static boolean matchesPattern(String fileName, Pattern pattern) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
}
