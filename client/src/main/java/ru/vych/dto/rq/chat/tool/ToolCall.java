package ru.vych.dto.rq.chat.tool;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс представляющий вызов инструмента в рамках чата с моделью.
 */
@Getter
@Setter
public class ToolCall {
    /**
     * Id вызова инструмента
     */
    private String id;


    /**
     * Описание вызываемого инструмента
     */
    private ToolFunction function;
}
