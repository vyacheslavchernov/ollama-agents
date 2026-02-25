package ru.vych.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обёртка для ответов с результатами работы инструментов, которые вызывает агент.
 * Позволяет быстро получить структурированный JSON ответ, который можно передать обратно агенту.
 */
@RequiredArgsConstructor
public class ToolJsonResponseWrapper {
    /**
     * Псевдоним инструмента, которому принадлежит этот ответ
     */
    @NonNull
    private final String TOOL_ALIAS;


    /**
     * Аргументы с которыми был вызван инструмент
     */
    @NonNull
    private final Map<String, Object> TOOL_ARGUMENTS;

    /**
     * Результат работы инструмента
     */
    private final Map<String, Object> CONTENT = new HashMap<>();


    /**
     * Ошибки, которые были получены во время работы инструмента
     */
    private final List<String> ERRORS = new ArrayList<>();

    private final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Добавить контент в ответ.
     * Контент будет отображён внутри поля "content"
     *
     * @param key  имя для добавляемого контента
     * @param item добавляемый контент (List, Map или какой-либо примитив)
     * @return текущий экземпляр {@link ToolJsonResponseWrapper}
     */
    public ToolJsonResponseWrapper addContent(String key, Object item) {
        CONTENT.put(key, item);
        return this;
    }

    /**
     * Добавить ошибку в ответ.
     * В ответе будет отображаться только верхне-уровневое сообщение ошибки.
     *
     * @param exceptions ошибка для добавления
     * @return текущий экземпляр {@link ToolJsonResponseWrapper}
     */
    public ToolJsonResponseWrapper addError(Exception... exceptions) {
        for (var e : exceptions) {
            ERRORS.add(e.getMessage());
        }
        return this;
    }

    /**
     * @return json строка сформированная на основе полей экземпляра
     */
    @SneakyThrows
    public String json() {
        return MAPPER.writeValueAsString(Map.of(
                "call_info", Map.of("called_tool", TOOL_ALIAS, "tool_arguments", TOOL_ARGUMENTS),
                "content", CONTENT,
                "errors", ERRORS
        ));
    }

    @Override
    public String toString() {
        return json();
    }
}
