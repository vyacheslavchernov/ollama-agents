package ru.vych.dto.rq.generate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.vych.dto.rq.ApiRequestDTO;

/**
 * DTO для запроса генерации ответа
 *
 * @see <a href="https://docs.ollama.com/api/generate">Api Reference</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public class GenerateRequestBody implements ApiRequestDTO {
    /**
     * Имя модели
     */
    private String model;

    /**
     * Текст на основе которого будет производиться генерация
     */
    private String prompt = null;

    /**
     * Используется для FIM-моделей. Текст, который идёт после запроса пользователя, но перед ответом модели.
     */
    private String suffix = null;

    /**
     * Изображения в формате Base64 для использования моделями, которые поддерживают работу с изображениями.
     */
    private String[] images = null;

    /**
     * Структура ответа модели, которую должна использовать модель при генерации.
     * Может быть конкретная JSON схема или просто "json".
     */
    private String format = null;

    /**
     * Системный промпт для модели
     */
    private String system = null;

    /**
     * Если true, возвращается поток с частичным ответом, который будет дополнять по мере генерации.
     */
    private Boolean stream = null;

    /**
     * Если true, возвращает блок размышлений модели в отдельном поле
     */
    private Boolean think = null;

    /**
     * Если true, модель получит сырой промпт в обход каких-либо шаблонизаторов.
     */
    private Boolean raw = null;

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

    /**
     * @param model Имя модели, которую нужно использовать для генерации
     */
    public GenerateRequestBody(String model) {
        this.model = model;
    }

}
