package ru.vych.dto.rq.model;

import ru.vych.dto.rq.ApiRequestDTO;

/**
 * DTO для запроса подробной информации о модели.
 *
 * @param model   имя модели
 * @param verbose если true, возвращает расширенные данные, включая подробности по слоям, токенизатору и параметрам
 * @see <a href="https://docs.ollama.com/api-reference/show-model-details">API Reference</a>
 */
public record ModelDetailsBody(String model, boolean verbose) implements ApiRequestDTO {
}
