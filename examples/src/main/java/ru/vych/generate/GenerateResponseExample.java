package ru.vych.generate;

import ru.vych.OllamaClient;
import ru.vych.dto.rq.generate.GenerateRequestBody;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static ru.vych.dto.rs.model.ModelCapabilities.COMPLETION;

/**
 * Пример генерации контента с помощью Ollama
 */
public class GenerateResponseExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Создание клиента
        var client = new OllamaClient();

        // Получение первой доступной модели в Ollama с нужными возможностями
        var model = client.getModelsByCapabilities(Set.of(COMPLETION)).getFirst();

        // Параметры генерации
        var generationParams = new GenerateRequestBody(model.getName())
                .setPrompt("Привет")
                .setKeepAlive("1m");

        // Не потоковая генерация
        var generation = client.generateResponse(generationParams);
        System.out.printf("Ответ от модели '%s':\n%s\n\n", generation.getModel(), generation.getResponse());

        // Потоковая генерация
        System.out.printf("Потоковая генерация ответа от модели '%s':\n", model.getModel());
        generationParams.setPrompt("Напиши один небольшой абзац текста на тему кошек.");
        client.generateAsyncResponse(generationParams).thenAccept(stream -> {
            try (stream) {
                stream.forEach(dto -> {
                    if (dto.getThinking() != null) System.out.print(dto.getThinking());
                    if (dto.getResponse() != null) System.out.print(dto.getResponse());
                });
            }
        }).get();
        System.out.println();
    }
}
