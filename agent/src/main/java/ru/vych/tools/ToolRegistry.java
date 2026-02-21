package ru.vych.tools;

import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rq.chat.tool.ToolFunction;
import ru.vych.dto.rq.chat.tool.ToolParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реестр инструментов для агентов.
 * Инструменты готовы к использованию при получении через методы `get`.
 */
// TODO: добавить возможность конфигурации через файлы
public class ToolRegistry {
    private static final Map<String, ToolDefinition> registry = new HashMap<>();

    static {
        //region files tools
        registry.put(
                "search_file",
                new ToolDefinition(new ToolFunction(
                        "Search files",
                        new ToolParameters()
                                .addProperty(
                                        "directoryPath", "string",
                                        "Путь к каталогу в котором нужно искать файлы. Обязательно убедись, " +
                                                "что путь составлен корректно для системы пользователя."
                                )
                                .addProperty(
                                        "pattern", "string",
                                        "Паттерн имени файла или каталога для поиска в формате регулярного " +
                                                "выражения формата Java. (не нужно указывать в паттерне " +
                                                "абсолютный или относительный путь)"
                                )
                                .addProperty(
                                        "recursive", "boolean",
                                        "Нужно ли производить рекурсивный поиск по вложенным каталогам. " +
                                                "Не используй рекурсивный поиск, если в этом нет явной необходимости"
                                ),
                        params -> FileTools.findFilesWithPattern(
                                (String) params.get("directoryPath"),
                                (String) params.get("pattern"),
                                (Boolean) params.get("recursive")
                        )
                ).setDescription("Произвести поиск файлов и\\или каталогов в каталоге по паттерну в " +
                        "виде регулярного выражения. Можно производить рекурсивный поиск по вложенным " +
                        "каталогам. Ответ возвращается в формате " +
                        "json - {\"founded\": [\"./file1.txt\", \"./file2.log\"], \"error\": \"\"}")
                )
        );

        registry.put(
                "read_text_file",
                new ToolDefinition(new ToolFunction(
                        "Read text file",
                        new ToolParameters()
                                .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы"),
                        params -> FileTools.readTextFileToString((String) params.get("filePath"))
                ).setDescription("Получить содержимое текстового файла. Если получить " +
                        "содержимое не удалось, то вернётся подробная ошибка.")
                )
        );

        registry.put(
                "overwrite_text_file",
                new ToolDefinition(new ToolFunction(
                        "Overwrite text file",
                        new ToolParameters()
                                .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы")
                                .addProperty("newContent", "string", "Новые данные, которыми будет перезаписан файл."),
                        params -> FileTools.overwriteFile((String) params.get("filePath"), (String) params.get("newContent"))
                ).setDescription("Изменить содержимое текстового файла. Полностью перезаписывает " +
                        "старые данные теми, что переданы в параметрах вызова. Запись произойдёт через " +
                        "Files.write(Paths.get(filePath), newContent.getBytes())")
                )
        );

        registry.put(
                "create_and_write_text_file",
                new ToolDefinition(
                        new ToolFunction(
                                "Create and write text file",
                                new ToolParameters()
                                        .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы")
                                        .addProperty("content", "string", "Данные, которые будут записаны файл"),
                                params -> FileTools.createFileIfNotExists((String) params.get("filePath"), (String) params.get("content"))
                        ).setDescription("Создать новый текстовый файл и записать в него данные. Перед созданием проверяется существует ли файл")
                )
        );

        registry.put(
                "create_directory",
                new ToolDefinition(
                        new ToolFunction(
                        "Create directory",
                        new ToolParameters()
                                .addProperty("dirPath", "string", "Абсолютный путь к каталогу в формате системы"),
                        params -> FileTools.createDirectoryIfNotExists((String) params.get("dirPath"))
                ).setDescription("Создать новый каталог. Перед созданием проверяется существует ли каталог")
                )
        );
        //endregion

        //region system tools
        registry.put(
                "system_info",
                new ToolDefinition(new ToolFunction(
                        "System info", null,
                        params -> SystemTools.collectSystemInfo()
                ).setDescription("Получить информацию о окружении и системе, а именно тип ОС, активного пользователя и пр.")
                )
        );
        //endregion
    }

    @SuppressWarnings("unused")
    public static ToolDefinition getTool(String toolName) {
        if (!registry.containsKey(toolName)) {
            throw new IllegalArgumentException("No such tool in registry: " + toolName);
        }
        return registry.get(toolName);
    }

    public static List<ToolDefinition> getToolsList(String... toolName) {
        var list = new ArrayList<ToolDefinition>();
        for (var name : toolName) {
            if (!registry.containsKey(name)) {
                throw new IllegalArgumentException("No such tool in registry: " + name);
            }
            list.add(registry.get(name));
        }
        return list;
    }
}
