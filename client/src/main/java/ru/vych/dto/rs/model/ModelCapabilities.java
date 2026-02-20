package ru.vych.dto.rs.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Перечисление возможностей, поддерживаемых моделью.
 */
public enum ModelCapabilities {
    /**
     * Модель может генерировать текст по подсказке
     */
    COMPLETION,

    /**
     * Модель поддерживает размышления
     */
    THINKING,

    /**
     * Поддерживает диалоговый формат
     */
    CHAT,

    /**
     * Может обрабатывать изображения (мультимодальная модель)
     */
    VISION,

    /**
     * Поддерживает создание векторных представлений текста
     */
    EMBEDDING,

    /**
     * Поддерживает использование инструментов
     */
    TOOLS;

    @JsonCreator
    public static ModelCapabilities fromString(String key) {
        return key == null ? null : ModelCapabilities.valueOf(key.toUpperCase());
    }
}
