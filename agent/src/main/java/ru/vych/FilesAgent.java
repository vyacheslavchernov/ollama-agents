package ru.vych;

import ru.vych.agent.AbstractAgent;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rq.chat.tool.ToolFunction;
import ru.vych.dto.rq.chat.tool.ToolParameters;
import ru.vych.dto.rs.model.Model;

import java.util.List;

public class FilesAgent extends AbstractAgent {

    public FilesAgent(OllamaClient client, Model model) {
        super(client, model);

        toolset = List.of(
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
                        params -> FileUtils.findFilesWithPattern(
                                (String) params.get("directoryPath"),
                                (String) params.get("pattern"),
                                (Boolean) params.get("recursive")
                        )
                ).setDescription("Произвести поиск файлов и\\или каталогов в каталоге по паттерну в " +
                        "виде регулярного выражения. Можно производить рекурсивный поиск по вложенным " +
                        "каталогам. Ответ возвращается в формате " +
                        "json - {\"founded\": [\"./file1.txt\", \"./file2.log\"], \"error\": \"\"}")
                ),
                new ToolDefinition(new ToolFunction(
                        "System info", null,
                        params -> SystemUtils.collectSystemInfo()
                ).setDescription("Получить информацию о окружении и системе, " +
                        "а именно тип ОС, активного пользователя и пр."
                )
                ),
                new ToolDefinition(new ToolFunction(
                        "Read text file",
                        new ToolParameters()
                                .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы"),
                        params -> FileUtils.readTextFileToString((String) params.get("filePath"))
                ).setDescription("Получить содержимое текстового файла. Если получить " +
                        "содержимое не удалось, то вернётся подробная ошибка.")
                ),
                new ToolDefinition(new ToolFunction(
                        "Overwrite text file",
                        new ToolParameters()
                                .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы")
                                .addProperty("newContent", "string", "Новые данные, которыми будет перезаписан файл."),
                        params -> FileUtils.overwriteFile((String) params.get("filePath"), (String) params.get("newContent"))
                ).setDescription("Изменить содержимое текстового файла. Полностью перезаписывает " +
                        "старые данные теми, что переданы в параметрах вызова. Запись произойдёт через " +
                        "Files.write(Paths.get(filePath), newContent.getBytes())")
                )
        );

        system("Ты - для работы с файловой системой. Твоя основная задача искать файлы, " +
                "анализировать их содержимое, если возможно, и вносить в них изменения. " +
                "Ты должен помогать пользователю в решении его задач, если они не противоречат " +
                "твоей основной задаче. Внимательно ознакомься с набором доступных " +
                "тебе инструментов. Ты должен как можно точнее выполнить задачу, " +
                "которую укажет пользователь в запросе." +
                "Если запрос не соответствует твоему набору инструментов, то сообщи об " +
                "этом пользователю и не выполняй никаких операций." +
                "Не повторяйся в своих рассуждениях и не зацикливайся. Действуй максимально " +
                "автономно и самостоятельно принимай не критичные " +
                "решения, если в запросе не указано иного."
        );
    }
}
