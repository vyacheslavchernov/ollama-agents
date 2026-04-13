package ru.vych.agent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.vych.OllamaClient;
import ru.vych.agent.config.agent.AgentConfig;
import ru.vych.agent.tools.ToolRegistry;
import ru.vych.agent.utils.ConfigUtils;
import ru.vych.agent.utils.DateTimeUtils;

/**
 * Конфигурируемый универсальный агент.
 * Конфигурируется с помощью YAML файла.
 * См. {@link AgentConfig}
 */
@Slf4j
@Getter
public class ConfigurableAgent extends AbstractAgent {
    private final AgentConfig config;

    public ConfigurableAgent(OllamaClient client, AgentConfig config) {
        super(client, client.getModelByName(config.getModel()), ToolRegistry.getToolsList(config.getToolset()));
        log.debug("Agent [{}] | Config - [{}]", this, config);
        this.config = config;
        system(ConfigUtils.readFileContentFromResources(config.getSystemPrompt()));
        system("Текущая дата (на момент начала чата): " + DateTimeUtils.getFormattedActualTime());
        system("Если пользователь в своём запросе не указал твой " +
                "рабочий каталог, то рабочий каталог по умолчанию `" +
                System.getProperty("user.dir") + "`");
    }
}
