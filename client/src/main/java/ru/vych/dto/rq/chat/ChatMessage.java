package ru.vych.dto.rq.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rq.chat.tool.ToolCall;

import java.util.List;
import java.util.UUID;

/**
 * Класс представляющий отдельное сообщение в чате с моделью
 */
@Getter
@Setter
public class ChatMessage {
    private final String uuid = String.valueOf(UUID.randomUUID());

    /**
     * Роль от которой было написано сообщение
     */
    private Role role;

    /**
     * Текст сообщения
     */
    private String content;

    /**
     * Размышления модели
     */
    private String thinking;

    /**
     * Изображения в формате Base64 передаваемые вместе с сообщением
     */
    private String[] images;

    /**
     * Вызовы инструментов полученные от модели вместе с ответом
     */
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    @JsonCreator
    public ChatMessage(
            @JsonProperty("role") Role role,
            @JsonProperty("content") String content
    ) {
        this.role = role;
        this.content = content;
    }
}
