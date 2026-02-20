package ru.vych.dto.rs.generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

/**
 * DTO для ответа метода генерации Ollama
 *
 * @see <a href="https://docs.ollama.com/api/generate">Api Reference</a>
 */
@Getter
@Setter
public class GenerateResponseBody implements ApiResponseDTO {
    /**
     * Имя модели, которая сгенерировала ответ
     */
    private String model;

    /**
     * Время генерации ответа в формате ISO 8601
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Текст ответа, который был сгенерирован моделью
     */
    private String response;

    /**
     * Тест размышлений модели в процессе генерации ответа.
     */
    private String thinking;

    /**
     * Флаг завершения генерации
     */
    private boolean done;

    /**
     * Причина завершения генерации.
     */
    @JsonProperty("done_reason")
    private String doneReason;

    /**
     * Время потраченное на генерацию ответа в наносекундах.
     */
    @JsonProperty("total_duration")
    private long totalDuration;

    /**
     * Время потраченное на загрузку модели в наносекундах.
     */
    @JsonProperty("load_duration")
    private long loadDuration;

    /**
     * Количество токенов, которые модель получила на вход вместе с запросом
     */
    @JsonProperty("prompt_eval_count")
    private long promptEvalCount;

    /**
     * Время, которое модель потратила на обработку запроса перед началом генерации, в наносекундах
     */
    @JsonProperty("prompt_eval_duration")
    private long promptEvalDuration;

    /**
     * Количество токенов, которые модель сгенерировала в ответ на запрос
     */
    @JsonProperty("eval_count")
    private long evalCount;

    /**
     * Время потраченное на генерацию токенов в наносекундах
     */
    @JsonProperty("eval_duration")
    private long evalDuration;

    /**
     * Описание ошибки при генерации, если она случилась.
     */
    private String error;

    /**
     * Описание ошибки при генерации, если она случилась.
     */
    private String[] context;
}
