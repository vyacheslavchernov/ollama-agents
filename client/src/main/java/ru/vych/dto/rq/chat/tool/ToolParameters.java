package ru.vych.dto.rq.chat.tool;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для описания схемы параметров инструмента (JSON-схема)
 */
@Getter
@Setter
@Accessors(chain = true)
public class ToolParameters {
    private final String type = "object";
    private final Map<String, ToolParametersProp> properties = new HashMap<>();
    private String[] required = null;

    /**
     * Добавить параметр в схему
     *
     * @param name        имя параметра
     * @param type        тип параметра (см. json типы)
     * @param description (описание параметра в человеке-читаемом формате)
     * @return объект описания схемы с добавленным параметром
     */
    public ToolParameters addProperty(String name, String type, String description) {
        properties.put(name, new ToolParametersProp(type, description));
        return this;
    }
}
