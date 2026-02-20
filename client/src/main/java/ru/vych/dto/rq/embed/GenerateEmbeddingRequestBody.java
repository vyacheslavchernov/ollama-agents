package ru.vych.dto.rq.embed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.vych.dto.rq.ApiRequestDTO;
import ru.vych.dto.rq.generate.GenerationOptions;

/**
 * DTO для запроса генерации эмбеддингов
 *
 * @see <a href="https://docs.ollama.com/api/embed">Api Reference</a>
 */
@Getter
@Setter
@RequiredArgsConstructor
public class GenerateEmbeddingRequestBody implements ApiRequestDTO {
    /**
     * Имя модели, которая будет использована для генерации эмбеддинга
     */
    private final String model;

    /**
     * Массив строк из которых будут сгенерированы эмбеддинги
     */
    private final String[] input;

    /**
     * Будет ли обрезаться входящий текст, если он выходит за пределы окна контекста.
     * По умолчанию установлен в true; При значении false будет
     * генерироваться ошибка, если произошёл выход за пределы контекста
     */
    private boolean truncate = true;

    /**
     * Размерность генерируемых эмбеддиногов.
     * Чем выше значение, тем более длинным и точным будет сгенерированный эмбеддинг.
     */
    private Integer dimensions = null;

    /**
     * Как долго модель останется загруженной в память после отдачи ответа.
     * Для примера "5m" или "0" для мгновенной выгрузки
     */
    @JsonProperty("keep_alive")
    private String keepAlive = null;

    /**
     * Дополнительные параметры генерации
     */
    private GenerationOptions options = null;
}
