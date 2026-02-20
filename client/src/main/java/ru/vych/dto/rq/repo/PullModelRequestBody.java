package ru.vych.dto.rq.repo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.vych.dto.rq.ApiRequestDTO;

/**
 * DTO для запроса загрузки модели в локальный репозиторий Ollama
 *
 * @see <a href="https://docs.ollama.com/api/pull">API Reference</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PullModelRequestBody implements ApiRequestDTO {
    /**
     * Имя модели для загрузки
     */
    private final String model;

    /**
     * Разрешить загрузку через небезопасные подключения
     */
    private Boolean insecure = null;

    /**
     * Потоковое получение статуса загрузки
     */
    private Boolean stream = true;
}
