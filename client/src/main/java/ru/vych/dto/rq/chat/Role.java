package ru.vych.dto.rq.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Перечисление доступных ролей для {@link ChatMessage}
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Role {
    /**
     * Системное сообщение.
     * Содержит инструкции и указания, которыми руководствуется модель при генерации ответов в чате.
     * <p>
     * Имеет высший приоритет при принятии решений (?)
     */
    SYSTEM,

    /**
     * Сообщение пользователя (запрос)
     */
    USER,

    /**
     * Сообщение от ассистента (модели)
     */
    ASSISTANT,

    /**
     * Сообщение от инструмента (результат работы инструмента)
     */
    TOOL;

    @JsonCreator
    public static Role fromString(String key) {
        return key == null ? null : Role.valueOf(key.toUpperCase());
    }
}
