package ru.vych.dto.rs.repo;

import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

/**
 * DTO для ответа со статусом операций в репозитории
 *
 * @see <a href="https://docs.ollama.com/api/pull">API Reference</a>
 */
@Getter
@Setter
public class StatusResponseBody implements ApiResponseDTO {
    /**
     * Текущий статус операции
     */
    private String status;

    /**
     * Уникальная хеш-сигнатура (SHA-256) модели.
     * Используется для проверки целостности и идентификации версии модели.
     */
    private String digest;

    /**
     * Размер загружаемой модели в байтах
     */
    private Long total;

    /**
     * Количество уже загруженных данных
     */
    private Long completed;
}
