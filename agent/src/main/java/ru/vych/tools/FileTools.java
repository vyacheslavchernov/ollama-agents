package ru.vych.tools;

import lombok.extern.slf4j.Slf4j;
import ru.vych.agent.tools.ToolJsonResponseWrapper;
import ru.vych.tools.file.FileSearchResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Набор методов, которые используются агентами
 * как инструменты для работы с файловой системой
 */
@SuppressWarnings("unused")
@Slf4j
public class FileTools {

    /**
     * Перезаписывает содержимое файла новым содержимым.
     * Если файл не существует, то создаётся новый.
     *
     * @param filePath Путь к файлу
     * @param content  Содержимое файла в виде строки
     * @return json со статусом выполнения операции
     */
    public static String writeFile(String filePath, String content) {
        var response = new ToolJsonResponseWrapper("write_text_file", Map.of(
                "filePath", filePath,
                "content", "<NOT_DISPLAYED_FOR_CONTEXT_SAVING>"
        ));

        try {
            Files.write(Paths.get(filePath), content.getBytes());
            response.addContent("status", String.format("Файл `%s` успешно записан. Записано %s байт", filePath, content.getBytes().length));
        } catch (IOException e) {
            log.error("Got exception on file writing", e);
            response.addError(e);
        }

        return response.json();
    }

    /**
     * Ищет файлы по регулярному выражению в заданном каталоге
     *
     * @param directoryPath путь к каталогу
     * @param pattern       регулярное выражение для поиска
     * @param recursive     флаг рекурсивного поиска
     * @return json с результатами поиска
     */
    public static String findFilesWithPattern(String directoryPath, String pattern, Boolean recursive) {
        var result = new ToolJsonResponseWrapper("search_files", Map.of(
                "directoryPath", directoryPath,
                "pattern", pattern,
                "recursive", recursive
        ));

        List<FileSearchResult> searchResults = search(directoryPath, pattern, recursive);

        List<String> foundedPaths = searchResults.stream()
                .map(FileSearchResult::getFilePath)
                .filter(Objects::nonNull)
                .toList();

        List<Exception> searchErrors = searchResults.stream()
                .map(FileSearchResult::getOccurredException)
                .filter(Objects::nonNull)
                .toList();

        result.addContent("founded", foundedPaths);
        result.addError(searchErrors.toArray(new Exception[0]));

        return result.json();
    }

    /**
     * Открывает текстовый файл по заданному пути, полностью считывает его содержимое в строку
     * и возвращает эту строку.
     *
     * @param filePath путь к файлу
     * @return json с содержимым файла, если он был найден
     */
    public static String readTextFileToString(String filePath) {
        var result = new ToolJsonResponseWrapper("read_text_file", Map.of(
                "filePath", filePath
        ));

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            result.addContent("file_content", String.join("\n", lines));
        } catch (IOException e) {
            log.error("Got exception on file reading", e);
            result.addError(e);
        }

        return result.json();
    }

    /**
     * Проверяет существование каталога. Если каталог не существует, создает его.
     *
     * @param directoryPath Путь к каталогу
     * @return json со статусом выполнения операции
     */
    public static String createDirectoryIfNotExists(String directoryPath) {
        var result = new ToolJsonResponseWrapper("create_directory", Map.of(
                "directoryPath", directoryPath
        ));

        if (directoryPath.isEmpty() || directoryPath == null) {
            result.addError(new IllegalArgumentException("Путь к директории не может быть пустым или null"));
            return result.json();
        }

        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
                result.addContent("status", String.format("Каталог `%s` успешно создан.", directoryPath));
            } else {
                result.addError(new IllegalArgumentException("Каталог `" + directoryPath + "` уже существует"));
            }

        } catch (IOException e) {
            log.error("Got exception on directory creating", e);
            result.addError(e);
        }

        return result.json();
    }

    /**
     * Поиск файлов и каталогов.
     *
     * @param directoryPath начальная точка поиска
     * @param pattern       regexp паттерн для поиска
     * @param recursive     флаг для поиска по вложенным каталогам
     * @return список результатов поиска, которые либо содержат путь до файла\каталога,
     * либо возникшую во время поиска ошибку
     */
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
                    log.error("Got exception on search", e);
                    return List.of(new FileSearchResult(null, e));
                }
            }

            return foundFiles;

        } catch (Exception e) {
            log.error("Got exception on search", e);
            return List.of(new FileSearchResult(null, e));
        }
    }

    private static boolean matchesPattern(String fileName, Pattern pattern) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find();
    }
}
