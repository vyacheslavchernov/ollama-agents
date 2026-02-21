package ru.vych.agent;

import lombok.Getter;
import ru.vych.OllamaClient;
import ru.vych.agent.config.agent.AgentConfig;
import ru.vych.agent.tools.ToolRegistry;
import ru.vych.agent.utils.ConfigUtils;

/**
 * Конфигурируемый универсальный агент.
 * Конфигурируется с помощью YAML файла.
 * См. {@link AgentConfig}
 */
@Getter
public class ConfigurableAgent extends AbstractAgent {
    private final AgentConfig config;

    public ConfigurableAgent(OllamaClient client, AgentConfig config) {
        super(client, client.getModelByName(config.getModel()), ToolRegistry.getToolsList(config.getToolset()));
        this.config = config;
        system(ConfigUtils.readFileContentFromResources(config.getSystemPrompt()));
    }
}
