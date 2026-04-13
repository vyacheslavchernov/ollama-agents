package ru.vych.tools;

import lombok.extern.slf4j.Slf4j;
import ru.vych.agent.tools.ToolJsonResponseWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Инструменты для работы агента с мета-памятью
 */
@Slf4j
public class MemoryTools {
    /**
     * Запись данных в файл с мета-памятью модели
     *
     * @param content данные для записи
     * @return структурированный ответ с результатом работы метода
     */
    public static String appendToMemories(String content) {
        var response = new ToolJsonResponseWrapper("memories_add", Map.of(
                "content", content
        ));

        try (FileWriter fw = new FileWriter("MEMORIES.md", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(content);
        } catch (IOException e) {
            log.error("Got exception on adding memories.", e);
            return response.addError(e).json();
        }

        return response.addContent("status", "Данные успешно добавлены в воспоминания").json();
    }

    /**
     * @return данные из файла с мета-воспоминаниями
     */
    public static String readMemories() {
        var response = new ToolJsonResponseWrapper("memories_read", new HashMap<>());

        try {
            response.addContent("memories", Files.readString(Path.of("MEMORIES.md")));
        } catch (IOException e) {
            response.addError(e);
            log.error("Got exception on reading memories.", e);
        }

        return response.json();
    }
}
