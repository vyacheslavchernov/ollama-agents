package ru.vych.dto.rq.repo;

import ru.vych.dto.rq.ApiRequestDTO;

/**
 * DTO для запроса загрузки модели в локальный репозиторий Ollama
 *
 * @param model Имя модели для удаления
 * @see <a href="https://docs.ollama.com/api/delete">API Reference</a>
 */
public record DeleteModelRequestBody(String model) implements ApiRequestDTO {
}
