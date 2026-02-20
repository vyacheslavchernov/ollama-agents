package ru.vych.dto.rs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Дополнительная информация о модели
 *
 * @see <a href="https://docs.ollama.com/api/tags">API Reference</a>
 */
@Getter
@Setter
public class ModelDetails {
    @JsonProperty("parent_model")
    private String parentModel;

    /**
     * Формат хранения модели
     */
    private String format;

    /**
     * Основное семейство (архитектура) модели
     */
    private String family;

    /**
     * Расширенный список семейств, к которым принадлежит модель
     */
    private String[] families;

    /**
     * Количество параметров (весов) модели.
     * Этот показатель характеризует ёмкость модели: чем больше параметров,
     * тем выше потенциальное качество, но тем больше ресурсные требования.
     */
    @JsonProperty("parameter_size")
    private String parameterSize;

    /**
     * Уровень квантования модели — то есть степень сжатия весов для уменьшения объёма и ускорения работы.
     * Чем меньше число после Q, тем компактнее модель, но возможно с некоторой потерей точности.
     */
    @JsonProperty("quantization_level")
    private String quantizationLevel;
}
