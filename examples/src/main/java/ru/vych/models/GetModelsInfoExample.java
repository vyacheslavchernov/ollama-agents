package ru.vych.models;

import ru.vych.OllamaClient;
import ru.vych.dto.rq.generate.GenerateRequestBody;

import java.util.Arrays;
import java.util.Set;

import static ru.vych.dto.rs.model.ModelCapabilities.COMPLETION;
import static ru.vych.dto.rs.model.ModelCapabilities.TOOLS;

/**
 * Пример получения списка доступных моделей,
 * дополнительной информации по этим моделям и списка загруженных в память моделей.
 */
public class GetModelsInfoExample {
    public static void main(String[] args) {
        // Создание клиента
        var client = new OllamaClient();

        // Запрос установленных в Ollama моделей
        var avail = client.availableModels();
        System.out.printf("Первая доступная модель в Ollama:\n%s\n\n", avail.getModels().getFirst());

        // Запрос расширенной информации о модели в Ollama
        var details = client.modelDetails(avail.getModels().getFirst());
        System.out.printf("Детальная информация по первой доступной модели в Ollama:\n%s\n\n", details);

        // Получение списка загруженных в видеопамять моделей
        client.generateResponse(new GenerateRequestBody(client.getModelsByCapabilities(Set.of(COMPLETION)).getFirst().getModel())
                .setPrompt("Hi!")
                .setKeepAlive("1m"));
        var loaded = client.loadedModels();
        System.out.println("Список моделей загруженных в видеопамять:");
        loaded.getModels().forEach(model -> System.out.printf(
                "Модель '%s' будет выгружена [%s]\n",
                model.getName(), model.getExpiresAt())
        );

        // Получение первой доступной модели с определёнными возможностями
        var toolsModel = client.getModelsByCapabilities(Set.of(TOOLS)).getFirst();
        toolsModel = client.modelDetails(toolsModel);
        if (toolsModel != null) {
            System.out.printf(
                    "\nМодель '%s' имеет следующие возможности: %s\n",
                    toolsModel.getModel(), toolsModel.getCapabilities()
            );
        }

        // Получение списка моделей определённого семейства
        var gemmaModels = client.getModelsByFamily("gemma");
        System.out.println("\nДоступные модели семейства gemma:");
        gemmaModels.forEach(model ->
                System.out.printf(
                        "Модель %s. Семейства %s | %s\n",
                        model.getModel(),
                        model.getDetails().getFamily(),
                        Arrays.toString(model.getDetails().getFamilies())
                )
        );

        // Получение конкретной модели по её полному имени
        var gemma4b = client.getModelByName("gemma3:4b");
        System.out.println("\n" + gemma4b);
    }
}
