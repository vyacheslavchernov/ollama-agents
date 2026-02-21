package ru.vych.agent.config.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import ru.vych.agent.config.PathType;
import ru.vych.agent.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс конфигурации для инструмента агента.
 */
@Getter
@AllArgsConstructor
public class ToolConfig {
    private String alias;
    private String name;
    private String description;
    private List<ToolParameterConfig> toolParameterConfigs;
    private String method;

    /**
     * @param path путь до конфига в папке ресурсов
     * @return экземпляр конфига загруженный из файла
     */
    @SuppressWarnings("unchecked")
    public static ToolConfig loadFromFile(String path, PathType pathType) {
        String yamlContent = pathType == PathType.RELATIVE_TO_RESOURCES
                ? ConfigUtils.readFileContentFromResources(path)
                : ConfigUtils.readFileContentFromAbsolutePath(path);
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        Map<String, Object> toolData = (Map<String, Object>) data.get("tool");

        List<ToolParameterConfig> loadedToolParams = null;
        if (toolData.get("parameters") != null) {
            loadedToolParams = new ArrayList<>();
            for (var paramMap : (List<Map<String, Object>>) toolData.get("parameters")) {
                var param = (Map<String, String>) paramMap.get("parameter");
                loadedToolParams.add(new ToolParameterConfig(
                        param.get("name"),
                        param.get("type"),
                        param.get("description")
                ));
            }
        }

        return new ToolConfig(
                (String) toolData.get("alias"),
                (String) toolData.get("name"),
                (String) toolData.get("description"),
                loadedToolParams,
                (String) toolData.get("method")
        );
    }
}
