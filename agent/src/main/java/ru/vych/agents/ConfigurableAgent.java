package ru.vych.agents;

import lombok.Getter;
import ru.vych.OllamaClient;
import ru.vych.agent.AbstractAgent;
import ru.vych.tools.ToolRegistry;
import ru.vych.utils.ConfigUtils;

/**
 * Конфигурируемый универсальный агент.
 * Конфигурируется с помощью YAML файла.
 * См. {@link AgentConfig}
 */
@Getter
public class ConfigurableAgent extends AbstractAgent {
    private final AgentConfig config;

    public ConfigurableAgent(OllamaClient client, AgentConfig config) {
        super(client, client.getModelByName(config.getModel()));
        this.config = config;
        toolset = ToolRegistry.getToolsList(config.getToolset());
        system(ConfigUtils.loadFileContent("config/agents/dev/system_prompt.md"));
    }
}
