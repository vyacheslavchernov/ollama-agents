package ru.vych.chat;

import ru.vych.OllamaClient;
import ru.vych.agent.ConfigurableAgent;
import ru.vych.agent.config.PathType;
import ru.vych.agent.config.agent.AgentConfig;
import ru.vych.agent.tools.ToolRegistry;

import java.util.Set;

import static ru.vych.dto.rs.model.ModelCapabilities.COMPLETION;
import static ru.vych.dto.rs.model.ModelCapabilities.TOOLS;

/**
 * Пример обмена сообщениями с моделью с удержанием контекста (чата).
 * Для более детальной информации по агенту см.
 * {@link ru.vych.agent.AbstractAgent}, {@link ru.vych.agent.ConfigurableAgent}, {@link CardTools},
 * и конфиги в соответствующих ресурсных папках этого модуля
 */
// todo: написать пример для использования асинхронного режима чата
public class ChatWithAgentExample {
    public static void main(String[] args) {
        // Создание клиента
        var client = new OllamaClient();

        // Загрузка конфигов инструментов
        ToolRegistry.scanResourcesForConfigs("config/tools");

        // Загрузка агента из конфига
        var bankAgent = new ConfigurableAgent(client, AgentConfig.loadFromFile(
                "config/agents/bank/config.yaml",
                PathType.RELATIVE_TO_RESOURCES
        ));

        bankAgent.chat("Спиши со счёта 50 рублей");
        bankAgent.chat("Зачисли на счёт 150 рублей");
        bankAgent.chat("Вычти со счёта 500000 рублей");

        bankAgent.getMessages().forEach(msg -> System.out.printf(
                        "\n=======================================\n<think>%s</think>\n\n<content>%s</content>\n",
                        msg.getThinking(), msg.getContent()
                )
        );
    }
}
