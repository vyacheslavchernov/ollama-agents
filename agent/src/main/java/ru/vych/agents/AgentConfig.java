package ru.vych.agents;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * DTO конфигурации агента.
 * Загружается из файла формата YAML
 */
@Getter
@Setter
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
     * Для списка доступных см. {@link ru.vych.tools.ToolRegistry}
     */
    private String[] toolset;

    /**
     * @param path путь до конфига в папке ресурсов
     * @return экземпляр конфига загруженный из файла
     */
    @SuppressWarnings("unchecked")
    public static AgentConfig loadFromFile(String path) {
        String yamlContent = ru.vych.utils.ConfigUtils.loadFileContent(path);
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        Map<String, Object> agentData = (Map<String, Object>) data.get("agent");

        AgentConfig config = new AgentConfig();
        config.setName((String) agentData.get("name"));
        config.setDescription((String) agentData.get("description"));
        config.setModel((String) agentData.get("model"));
        config.setSystemPrompt((String) agentData.get("system_prompt"));
        config.setToolset(
                ((List<String>) agentData.get("toolset")).toArray(new String[0])
        );

        return config;
    }
}
