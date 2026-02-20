package ru.vych.dto.rs.version;

import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

/**
 * DTO для ответа API Ollama на запрос версии.
 *
 * @see <a href="https://docs.ollama.com/api-reference/get-version">API Reference</a>
 */
@Getter
@Setter
public class Version implements ApiResponseDTO {
    /**
     * Версия Ollama
     */
    private String version;
}
