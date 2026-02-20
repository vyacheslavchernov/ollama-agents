package ru.vych.dto.rq.generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Класс с описанием параметров генерации
 */
@Getter
@Setter
@Accessors(chain = true)
public class GenerationOptions {
    /**
     * Зерно для генерации повторяющихся ответов
     */
    private int seed;

    /**
     * Случайность генерации (креативность модели). Чем выше число, тем больше креативность
     */
    private float temperature;

    /**
     * Параметр ограничивает выбор следующего токена (слова или части слова) только K самыми вероятными вариантами
     */
    @JsonProperty("top_k")
    private int topK;

    /**
     * Модель сортирует все возможные токены по вероятности (от наиболее к наименее вероятным)
     * и берёт только те, чьи суммарные вероятности <= p
     */
    @JsonProperty("top_p")
    private float topP;

    /**
     * Минимальный порог вероятности, ниже которого токены исключаются из выбора при генерации текста
     */
    @JsonProperty("min_p")
    private float minP;

    /**
     * Специальные последовательности символов или слов, при появлении которых модель прекращает генерацию текст
     */
    private String[] stop;

    /**
     * Максимальная длина контекста для модели (количество токенов)
     */
    @JsonProperty("num_ctx")
    private float numCtx;

    /**
     * Максимальная длина ответа модели (в токенах)
     */
    @JsonProperty("num_predict")
    private float numPredict;
}
