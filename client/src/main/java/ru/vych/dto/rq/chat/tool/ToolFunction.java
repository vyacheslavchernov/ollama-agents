package ru.vych.dto.rq.chat.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.function.Function;

/**
 * Описание функционала инструмента
 */
@Getter
@Setter
@Accessors(chain = true)
public class ToolFunction {
    /**
     * Индекс вызова
     */
    private int index;

    /**
     * Имя инструмента
     */
    private String name;

    /**
     * Перечисление параметров, которы должны передаваться в инструмент в формате JSON
     */
    private ToolParameters parameters;

    /**
     * Перечисление аргументов, переданных в инструмент в формате JSON
     */
    private Map<String, Object> arguments;

    /**
     * Описание инструмента в человеко-читаемом формате
     */
    private String description;

    /**
     * Лямбда, которая будет вызвана при использовании инструмента.
     * Должна возвращать строку, которая будет передана обратно модели как результат работы инструмента
     */
    private Function<Map<String, Object>, String> function;

    public ToolFunction(
            @JsonProperty("name") String name,
            @JsonProperty("parameters") ToolParameters parameters,
            @JsonProperty("function") Function<Map<String, Object>, String> function
    ) {
        this.name = name;
        this.parameters = parameters;
        this.function = function;
    }

    @Override
    public String toString() {
        return "ToolFunction{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                ", description='" + description + '\'' +
                '}';
    }
}
