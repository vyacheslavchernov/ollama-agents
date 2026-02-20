package ru.vych.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.vych.OllamaClient;
import ru.vych.dto.rq.chat.ChatMessage;
import ru.vych.dto.rq.chat.ChatRequestBody;
import ru.vych.dto.rq.chat.tool.ToolCall;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rs.chat.ChatResponse;
import ru.vych.dto.rs.model.Model;

import java.util.ArrayList;
import java.util.List;

import static ru.vych.dto.rq.chat.Role.*;

/**
 * Абстрактная реализация интерфейса агента от которой можно наследовать своих агентов.
 */
@Accessors(chain = true)
public abstract class AbstractAgent implements Agent {
    private final OllamaClient client;

    /**
     * Основная модель, которая будет задействована для обработки запросов в ходе чата с агентом
     */
    @Getter
    @Setter
    protected Model model;

    /**
     * Набор инструментов, который доступен агенту.
     * Следует убедиться, что модель поддерживает "tools" прежде, чем добавлять инструменты
     *
     * @see Model#getCapabilities()
     */
    @Getter
    @Setter
    protected List<ToolDefinition> toolset = null;

    @Getter
    protected final List<ChatMessage> messages = new ArrayList<>();

    public AbstractAgent(OllamaClient client, Model model) {
        this.client = client;
        this.model = model;
    }


    public ChatResponse chat(String message) {
        messages.add(new ChatMessage(USER, message));
        var response = client.proceedChat(new ChatRequestBody(model.getName())
                .setMessages(messages)
                .setTools(toolset)
        );

        while (true) {
            messages.add(response.getMessage());
            if (response.getMessage().getToolCalls() == null) {
                return response;
            }

            response.getMessage().getToolCalls().forEach(this::callTool);

            response = client.proceedChat(new ChatRequestBody(model.getName())
                    .setMessages(messages)
                    .setTools(toolset)
            );
        }
    }

    public void system(String prompt) {
        messages.add(new ChatMessage(SYSTEM, prompt));
    }

    /**
     * Запустить инструмент запрошенный моделью
     * и добавить результат его работы в сообщения чата с агентом.
     *
     * @param call вызов инструмента
     */
    private void callTool(ToolCall call) {
        toolset.stream()
                .filter(toolDefinition ->
                        toolDefinition.getFunction().getName().equals(call.getFunction().getName())
                )
                .findFirst().ifPresent(toolDefinition -> {
                            var result = toolDefinition.getFunction().getFunction().apply(call.getFunction().getArguments());
                            messages.add(new ChatMessage(TOOL, result));
                        }
                );
    }
}
