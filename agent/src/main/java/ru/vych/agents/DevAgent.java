package ru.vych.agents;

import ru.vych.OllamaClient;
import ru.vych.agent.AbstractAgent;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rq.chat.tool.ToolFunction;
import ru.vych.dto.rq.chat.tool.ToolParameters;
import ru.vych.dto.rs.model.Model;
import ru.vych.tools.FileUtils;
import ru.vych.tools.SystemUtils;

import java.util.List;

public class DevAgent extends AbstractAgent {

    public DevAgent(OllamaClient client, Model model) {
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
                ),
                new ToolDefinition(new ToolFunction(
                        "Create and write text file",
                        new ToolParameters()
                                .addProperty("filePath", "string", "Абсолютный путь к файлу в формате системы")
                                .addProperty("content", "string", "Данные, которые будут записаны файл"),
                        params -> FileUtils.createFileIfNotExists((String) params.get("filePath"), (String) params.get("content"))
                ).setDescription("Создать новый текстовый файл и записать в него данные. Перед созданием проверяется существует ли файл")
                ),
                new ToolDefinition(new ToolFunction(
                        "Create directory",
                        new ToolParameters()
                                .addProperty("dirPath", "string", "Абсолютный путь к каталогу в формате системы"),
                        params -> FileUtils.createDirectoryIfNotExists((String) params.get("dirPath"))
                ).setDescription("Создать новый каталог. Перед созданием проверяется существует ли каталог")
                )
        );

        system("Роль:\n" +
                "Вы — автономный агент разработчика ПО.\n" +
                "\n" +
                "Задача:\n" +
                "Выполнять самостоятельную разработку программного обеспечения по запросам пользователя.\n" +
                "Выполнять анализ кода по запросам пользователя.\n" +
                "Давать рекомендации по улучшению кода на основе самостоятельного анализа и\\или запросов пользователя.\n" +
                "Самостоятельно применять эти рекомендации в кодовой базе, если пользователь укажет это в запросе.\n" +
                "\n" +
                "Принципы работы:\n" +
                "\n" +
                "Использование инструментов:\n" +
                "\n" +
                "Внимательно изучите доступные инструменты.\n" +
                "Используйте их для достижения целей запроса.\n" +
                "Если имеющихся инструментов недостаточно для достижения целей запроса, то сообщи об этом пользователю.\n" +
                "Автономность:\n" +
                "\n" +
                "Максимально автономны.\n" +
                "Проводите анализ кода, файлов и других ресурсов, если это необходимо.\n" +
                "Принимайте решения, не требующие внешнего вмешательства.\n" +
                "Эффективность:\n" +
                "\n" +
                "Избегайте цикличных размышлений (например, \"Но, ...\", \"Однако, ...\" и другие).\n" +
                "Не повторяйте рассуждения.\n" +
                "Используйте доступный контекст максимально эффективно.\n" +
                "Ограничения:\n" +
                "\n" +
                "Не уходите в избыточные детали, если они не критичны для задачи.\n" +
                "Если запрос пользователя не входит в список твоих задач, то отклоняй такой запрос подробно описав причину отказа..\n" +
                "Следуйте логике, основанной на предоставленной информации.");
    }
}
