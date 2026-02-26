package ru.vych.agent.config.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import ru.vych.agent.config.PathType;
import ru.vych.agent.tools.ToolRegistry;
import ru.vych.agent.utils.ConfigUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * DTO конфигурации агента.
 * Загружается из файла формата YAML
 */
@Getter
@AllArgsConstructor
public class AgentConfig {
    /**
     * Имя агента
     */
    private String name;

    /**
     * Описание функций агента
     */
    private String description;

    /**
     * Модель, которая будет использоваться при работе агента
     */
    private String model;

    /**
     * Путь до файла с системной инструкцией для агента
     */
    private String systemPrompt;

    /**
     * Перечисление инструментов, которые будут доступны агенту для работы.
     * Для списка доступных см. {@link ToolRegistry}
     */
    private String[] toolset;

    /**
     * @param path     путь до конфига в папке ресурсов
     * @param pathType тип пути (относительно папки ресурсов или абсолютный)
     * @return экземпляр конфига загруженный из файла
     */
    @SuppressWarnings("unchecked")
    public static AgentConfig loadFromFile(String path, PathType pathType) {
        String yamlContent = pathType == PathType.RELATIVE_TO_RESOURCES
                ? ConfigUtils.readFileContentFromResources(path)
                : ConfigUtils.readFileContentFromAbsolutePath(path);

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        Map<String, Object> agentData = (Map<String, Object>) data.get("agent");

        return new AgentConfig(
                (String) agentData.get("name"),
                (String) agentData.get("description"),
                (String) agentData.get("model"),
                (String) agentData.get("system_prompt"),
                ((List<String>) agentData.get("toolset")).toArray(new String[0])
        );
    }

    @Override
    public String toString() {
        return "AgentConfig{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", model='" + model + '\'' +
                ", toolset=" + Arrays.toString(toolset) +
                '}';
    }
}
