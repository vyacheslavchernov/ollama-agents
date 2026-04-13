package ru.vych.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import ru.vych.OllamaClient;
import ru.vych.dto.rq.chat.ChatMessage;
import ru.vych.dto.rq.chat.ChatRequestBody;
import ru.vych.dto.rq.chat.tool.ToolCall;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rs.chat.ChatResponse;
import ru.vych.dto.rs.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.vych.agent.ChatGenerationStage.*;
import static ru.vych.dto.rq.chat.Role.*;

/**
 * Абстрактная реализация интерфейса агента от которой можно наследовать своих агентов.
 */
@Slf4j
@Accessors(chain = true)
public abstract class AbstractAgent implements Agent {
    private final OllamaClient client;

    /**
     * Основная модель, которая будет задействована для обработки запросов в ходе чата с агентом
     */
    @Getter
    protected final Model model;

    /**
     * Набор инструментов, который доступен агенту.
     * Следует убедиться, что модель поддерживает "tools" прежде, чем добавлять инструменты
     *
     * @see Model#getCapabilities()
     */
    @Getter
    protected final List<ToolDefinition> toolset;

    @Getter
    protected final List<ChatMessage> messages = new ArrayList<>();

    /**
     * Флаг активности агента. Если `true`, то агент
     * занят какой-то задачей и не сможет обработать очередной запрос.
     */
    @Getter
    protected boolean working = false;

    /**
     * Текущая стадия генерации ответа
     */
    @Getter
    protected ChatGenerationStage generationStage = null;

    /**
     * Callback, который вызывается каждый раз,
     * когда добавляется новое сообщение в чате.
     * На вход получает добавляемое сообщение.
     */
    @Setter
    protected Consumer<ChatMessage> messagesUpdateCallback = null;

    /**
     * Callback, который вызывается каждый раз,
     * когда обрабатывается новая часть ответа от Ollama.
     * На вход получает очередную часть ответа и текущую стадию генерации.
     */
    @Setter
    protected BiConsumer<ChatResponse, ChatGenerationStage> asyncChatResponseGenerationCallback = null;

    /**
     * Callback, который вызывается каждый раз,
     * после вызова инструмента агентом.
     * На вход получает описание вызова (с параметрами) и результат
     * работы инструмента.
     */
    @Setter
    protected BiConsumer<ToolDefinition, String> toolInvokeCallback = null;

    public AbstractAgent(OllamaClient client, Model model, List<ToolDefinition> toolset) {
        this.client = client;
        this.model = model;
        this.toolset = toolset;
        log.debug("Agent [{}] | Instantiated ", this);
    }

    @Override
    public ChatResponse chat(String message) {
        log.debug("Agent [{}] | Got message `{}`", this, message);
        checkBeforeRunTask();
        working = true;
        generationStage = STARTED;
        try {
            addMessageToStorage(new ChatMessage(USER, message));
            var response = client.proceedChat(new ChatRequestBody(model.getName())
                    .setMessages(messages)
                    .setTools(toolset)
            );

            while (true) {
                addMessageToStorage(response.getMessage());
                if (response.getMessage().getToolCalls() == null) {
                    return response;
                }

                response.getMessage().getToolCalls().forEach(this::callTool);

                response = client.proceedChat(new ChatRequestBody(model.getName())
                        .setMessages(messages)
                        .setTools(toolset)
                );
            }
        } finally {
            generationStage = NON_ACTIVE;
            working = false;
        }
    }

    @Override
    @SneakyThrows
    public CompletableFuture<ChatResponse> chatAsync(String message) {
        log.debug("Agent [{}] | Got async message `{}`", this, message);
        checkBeforeRunTask();
        working = true;
        try {
            generationStage = STARTED;
            addMessageToStorage(new ChatMessage(USER, message));
            CompletableFuture<ChatResponse> completeFuture = new CompletableFuture<>();
            client.proceedAsyncChat(new ChatRequestBody(model.getModel())
                            .setMessages(messages)
                            .setTools(toolset))
                    .thenAccept(stream ->
                            completeFuture.complete(processPartialResponseStream(stream)));

            // Получение итогового результата
            var completeResponse = completeFuture.get();

            while (true) {
                addMessageToStorage(completeResponse.getMessage());
                if (completeResponse.getMessage().getToolCalls() == null || completeResponse.getMessage().getToolCalls().isEmpty()) {
                    return CompletableFuture.completedFuture(completeResponse);
                }

                completeResponse.getMessage().getToolCalls().forEach(this::callTool);

                generationStage = STARTED;
                CompletableFuture<ChatResponse> completeFutureInner = new CompletableFuture<>();
                client.proceedAsyncChat(new ChatRequestBody(model.getModel())
                                .setMessages(messages)
                                .setTools(toolset))
                        .thenAccept(stream ->
                                completeFutureInner.complete(processPartialResponseStream(stream)));

                completeResponse = completeFutureInner.get();
            }
        } finally {
            generationStage = NON_ACTIVE;
            working = false;
        }
    }

    @Override
    public void system(String prompt) {
        log.debug("Agent [{}] | Got system prompt `{}`", this, prompt);
        messages.add(new ChatMessage(SYSTEM, prompt));
    }

    @Override
    public void clearMessages() {
        messages.removeIf(message -> message.getRole() != SYSTEM);
    }

    /**
     * Запустить инструмент запрошенный моделью
     * и добавить результат его работы в сообщения чата с агентом.
     *
     * @param call вызов инструмента
     */
    private void callTool(ToolCall call) {
        log.debug("Agent [{}] | Got tool call `{}`", this, call);
        toolset.stream()
                .filter(toolDefinition ->
                        toolDefinition.getFunction().getName().equals(call.getFunction().getName())
                )
                .findFirst().ifPresent(toolDefinition -> {
                            var result = toolDefinition.getFunction().getFunction().apply(call.getFunction().getArguments());
                            log.debug(
                                    "Agent [{}] | Got result on tool call with id [{}] | Result [{}]",
                                    this, call.getId(), result
                            );
                            if (toolInvokeCallback != null) {
                                toolInvokeCallback.accept(toolDefinition, result);
                            }
                            addMessageToStorage(new ChatMessage(TOOL, result));
                        }
                );
    }

    private void addMessageToStorage(ChatMessage message) {
        if (messagesUpdateCallback != null) messagesUpdateCallback.accept(message);
        messages.add(message);
    }

    /**
     * Обработка потока частичных ответов от Ollama в режиме чата
     *
     * @param stream поток частичных ответов
     * @return конечное сообщение на основе частей
     */
    private ChatResponse processPartialResponseStream(Stream<ChatResponse> stream) {
        List<ChatResponse> responses = new ArrayList<>();

        // Последовательно обрабатываем частичные ответы
        stream.forEach(rs -> {
            if (rs.getError() != null) {
                throw new RuntimeException("Error on generation: " + rs.getError());
            }

            if (rs.isDone()) {
                generationStage = DONE;
            }

            if (asyncChatResponseGenerationCallback != null) {
                asyncChatResponseGenerationCallback.accept(rs, generationStage);
            }
            responses.add(rs);

            switch (generationStage) {
                case DONE -> {
                }
                case STARTED -> generationStage = THINKING;
                case THINKING -> {
                    if (rs.getMessage().getThinking() == null) {
                        generationStage = GENERATING_RESPONSE;
                    }
                }
            }
        });

        // Сборка полных строк размышления и ответа
        StringBuilder thinking = new StringBuilder();
        StringBuilder content = new StringBuilder();
        for (ChatResponse response : responses) {
            if (response.getMessage().getThinking() != null) {
                thinking.append(response.getMessage().getThinking());
            }
            if (response.getMessage().getContent() != null) {
                content.append(response.getMessage().getContent());
            }
        }

        // Создаем финальное сообщение
        ChatResponse finalResponse = responses.getLast();
        finalResponse.getMessage().setThinking(thinking.toString());
        finalResponse.getMessage().setContent(content.toString());

        finalResponse.getMessage().setToolCalls(responses.stream()
                .filter(rs -> rs.getMessage().getToolCalls() != null)
                .flatMap(rs -> rs.getMessage().getToolCalls().stream())
                .collect(Collectors.toList()));

        return finalResponse;
    }

    private void checkBeforeRunTask() {
        if (working) {
            throw new RuntimeException("Agent already working on task");
        }
    }
}
