package ru.vych.dto.rq.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.vych.dto.rq.ApiRequestDTO;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rq.generate.GenerationOptions;

import java.util.List;

/**
 * DTO для запроса генерации ответа в режиме чата (с удержанием контекста)
 *
 * @see <a href="https://docs.ollama.com/api/chat">Api Reference</a>
 */
@Getter
@Setter
@Accessors(chain = true)
public class ChatRequestBody implements ApiRequestDTO {
    /**
     * Имя модели
     */
    private String model;

    /**
     * История чата разделённая по ролям
     */
    private List<ChatMessage> messages;

    /**
     * Список инструментов доступных для модели.
     */
    private List<ToolDefinition> tools;

    /**
     * Дополнительные параметры генерации
     */
    private GenerationOptions options = null;

    /**
     * Если true, возвращается поток с частичным ответом, который будет дополнять по мере генерации.
     */
    private Boolean stream = null;

    /**
     * Если true, возвращает блок размышлений модели в отдельном поле
     */
    private Boolean think = null;

    /**
     * Как долго модель останется загруженной в память после отдачи ответа.
     * Для примера "5m" или "0" для мгновенной выгрузки
     */
    @JsonProperty("keep_alive")
    private String keepAlive = null;

    /**
     * Структура ответа модели, которую должна использовать модель при генерации.
     * Может быть конкретная JSON схема или просто "json".
     */
    private String format = null;

    /**
     * @param model Имя модели, которую нужно использовать для генерации
     */
    public ChatRequestBody(String model) {
        this.model = model;
    }
}
