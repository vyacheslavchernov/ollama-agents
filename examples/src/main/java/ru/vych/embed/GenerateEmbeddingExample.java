package ru.vych.embed;

import ru.vych.OllamaClient;
import ru.vych.dto.rq.embed.GenerateEmbeddingRequestBody;

import java.util.Set;

import static ru.vych.dto.rs.model.ModelCapabilities.EMBEDDING;

/**
 * Пример генерации эмбеддингов с помощью Ollama
 */
public class GenerateEmbeddingExample {
    public static void main(String[] args) {
        // Создание клиента
        var client = new OllamaClient();

        // Получение первой доступной модели в Ollama с нужными возможностями
        var model = client.getModelsByCapabilities(Set.of(EMBEDDING)).getFirst();

        // Параметры генерации
        var text = "Текст из которого будет сгенерирован эмбеддинг";
        System.out.printf("Исходный текст: \"%s\"\n\n", text);
        var generationParams = new GenerateEmbeddingRequestBody(
                model.getModel(),
                new String[]{text}
        );

        // Генерация эмбеддинга
        var generation = client.generateEmbedding(generationParams);
        System.out.printf("Сгенерированные эмбеддинги:\n%s", generation.getEmbeddings());
    }
}
