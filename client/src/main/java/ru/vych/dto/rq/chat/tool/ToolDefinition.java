package ru.vych.dto.rq.chat.tool;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс описывающий определение инструмента,
 * который может использовать модель в ходе своей работы.
 */
@Getter
@Setter
public class ToolDefinition {
    /**
     * Тип инструмента (всегда "function")
     */
    private final String type = "function";

    /**
     * Описание функционала инструмента
     */
    private ToolFunction function;

    public ToolDefinition(ToolFunction function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return function.getName();
    }
}
