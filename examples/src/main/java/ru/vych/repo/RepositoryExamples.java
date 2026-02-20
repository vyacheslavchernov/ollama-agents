package ru.vych.repo;

import ru.vych.OllamaClient;
import ru.vych.dto.rq.repo.DeleteModelRequestBody;
import ru.vych.dto.rq.repo.PullModelRequestBody;

import java.util.concurrent.ExecutionException;

/**
 * Пример управления моделями в локальном репозитории Ollama
 */
public class RepositoryExamples {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Создание клиента
        var client = new OllamaClient();

        // Установка параметров генерации
        var modelName = "qwen3:0.6b";
        var generationParams = new PullModelRequestBody(modelName);

        // Загрузка модели в локальный репозиторий с отслеживанием статуса загрузки
        client.pullModelAsync(generationParams).thenAccept(stream -> {
            try (stream) {
                stream.forEach(dto ->
                        System.out.printf("Текущий статус загрузки модели '%s' | %s\n", modelName, dto.getStatus())
                );
            }
        }).get();

        // Удаление ранее скачанной модели
        var rs = client.deleteModel(new DeleteModelRequestBody(modelName));
        if (rs.getCode() == 200) {
            System.out.printf("Модель '%s' успешно удалена\n", modelName);
        } else {
            System.out.printf("Не удалось удалить модель - статус ответа [%s]\n", rs.getCode());
        }
    }
}
