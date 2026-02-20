package ru.vych.dto.rs.model;

import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

import java.util.List;

/**
 * DTO для ответа API Ollama на запрос списка моделей.
 *
 * @see <a href="https://docs.ollama.com/api/tags">API Reference TAGS</a>
 * @see <a href="https://docs.ollama.com/api/ps">API Reference PS</a>
 */
@Getter
@Setter
public class ModelsList implements ApiResponseDTO {
    /**
     * Список моделей
     */
    private List<Model> models;
}
