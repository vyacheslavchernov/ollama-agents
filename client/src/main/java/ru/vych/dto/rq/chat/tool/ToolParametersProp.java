package ru.vych.dto.rq.chat.tool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Описание параметра для схемы параметров инструмента
 */
@Getter
@Setter
@AllArgsConstructor
public class ToolParametersProp {
    /**
     * Тип параметра (см. json типы)
     */
    private String type;


    /**
     * Описание параметра в человеко-читаемом формате.
     */
    private String description;
}
