package ru.vych.dto.rs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.vych.dto.rs.ApiResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DTO с информацией о модели
 *
 * @see <a href="https://docs.ollama.com/api/ps">API Reference PS</a>
 * @see <a href="https://docs.ollama.com/api/tags">API Reference TAGS</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Model implements ApiResponseDTO {
    /**
     * Параметры конфигурации модели по умолчанию.
     * <p>
     * Обычно включает:
     * <ul>
     *     <li>"temperature" — параметр “творчества” модели (0.7 означает умеренную вариативность ответов);</li>
     *     <li>"num_ctx" — максимальная длина контекста (в токенах), которую модель может учитывать;</li>
     * </ul>
     */
    private String parameters;

    /**
     * Текст лицензии или условий использования модели, предоставленных разработчиком.
     */
    private String license;

    /**
     * Список возможностей, поддерживаемых моделью.
     */
    private List<ModelCapabilities> capabilities;

    /**
     * Имя модели
     */
    private String name;

    /**
     * Имя модели
     */
    private String model;

    /**
     * Дата последней модификации модели в формате ISO 8601
     */
    @JsonProperty("modified_at")
    private String modifiedAt;

    /**
     * Общий размер модели занимаемый на диске в байтах.
     * Включает все веса, квантованные параметры и метаданные.
     */
    private Long size;

    /**
     * Уникальная хеш-сигнатура (SHA-256) модели.
     * Используется для проверки целостности и идентификации версии модели.
     */
    private String digest;

    /**
     * Дополнительная информация о модели
     */
    private ModelDetails details;

    /**
     * Шаблон, определяющий, как Ollama формирует входную подсказку для модели при запросах через API
     */
    private String template;

    /**
     * Дополнительные мета-данные модели
     */
    @JsonProperty("model_info")
    private Map<String, String> modelInfo;

    /**
     * Время, когда модель будет выгружена из памяти
     */
    @JsonProperty("expires_at")
    private String expiresAt;

    /**
     * Объём видеопамяти, который занимает модель в байтах
     */
    @JsonProperty("size_vram")
    private Long sizeVram;

    /**
     * Длина контекста модели
     */
    @JsonProperty("context_length")
    private Long contextLength;

    /**
     * Скопировать подробные данные из одной модели в другую.
     *
     * @param from объект из которого будет производиться копирование
     * @return текущий объект со скопированными данными из <i>from</i>
     */
    public Model copy(Model from) {
        parameters = from.getParameters() != null ? from.getParameters() : parameters;
        license = from.getLicense() != null ? from.getLicense() : license;
        capabilities = from.getCapabilities() != null ? from.getCapabilities() : capabilities;
        name = from.getName() != null ? from.getName() : name;
        model = from.getModel() != null ? from.getModel() : model;
        modifiedAt = from.getModifiedAt() != null ? from.getModifiedAt() : modifiedAt;
        size = from.getSize() != null ? from.getSize() : size;
        digest = from.getDigest() != null ? from.getDigest() : digest;
        details = from.getDetails() != null ? from.getDetails() : details;
        template = from.getTemplate() != null ? from.getTemplate() : template;
        modelInfo = from.getModelInfo() != null ? from.getModelInfo() : modelInfo;
        expiresAt = from.getExpiresAt() != null ? from.getExpiresAt() : expiresAt;
        sizeVram = from.getSizeVram() != null ? from.getSizeVram() : sizeVram;
        contextLength = from.getContextLength() != null ? from.getContextLength() : contextLength;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Model model1 = (Model) o;
        return Objects.equals(capabilities, model1.capabilities) && Objects.equals(name, model1.name)
                && Objects.equals(model, model1.model) && Objects.equals(modifiedAt, model1.modifiedAt)
                && Objects.equals(details, model1.details) && Objects.equals(modelInfo, model1.modelInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capabilities, name, model, modifiedAt, details, modelInfo);
    }

    @Override
    public String toString() {
        return "Model{\n" +
                "\tparameters='" + parameters + "',\n" +
                "\tlicense='" + license + "',\n" +
                "\tcapabilities=" + capabilities + ",\n" +
                "\tname='" + name + "',\n" +
                "\tmodel='" + model + "',\n" +
                "\tmodifiedAt='" + modifiedAt + "',\n" +
                "\tsize=" + size + ",\n" +
                "\tdigest='" + digest + ",'\n" +
                "\tdetails=" + details + ",\n" +
                "\ttemplate='" + template + ",'\n" +
                "\tmodelInfo=" + modelInfo + ",\n" +
                "\texpiresAt='" + expiresAt + ",'\n" +
                "\tsizeVram=" + sizeVram + ",\n" +
                "\tcontextLength=" + contextLength + ",\n" +
                '}';
    }
}
