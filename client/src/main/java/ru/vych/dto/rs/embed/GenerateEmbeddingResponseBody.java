package ru.vych.dto.rs.embed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

import java.util.List;

/**
 * DTO для ответа генерации эмбеддинга
 *
 * @see <a href="https://docs.ollama.com/api/embed">Api Reference</a>
 */
@Getter
@Setter
public class GenerateEmbeddingResponseBody implements ApiResponseDTO {
    /**
     * Имя модели, которая сгенерировала ответ
     */
    private String model;

    /**
     * Список векторов эмбеддингов, которые сгенерировала модель
     */
    private List<List<Double>> embeddings;

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
}
