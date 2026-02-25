package ru.vych.agent;

import ru.vych.dto.rs.chat.ChatResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс агента с возможностью чата и использования инструментов.
 */
public interface Agent {
    /**
     * Отправить сообщение в чат с агентом от роли {@link ru.vych.dto.rq.chat.Role#USER}
     *
     * @param message текст сообщения
     * @return сгенерированный ответ от агента.
     */
    ChatResponse chat(String message);

    /**
     * Отправить сообщение в чат с агентом от роли {@link ru.vych.dto.rq.chat.Role#USER}
     *
     * @param message текст сообщения
     * @return сгенерированный ответ от агента обёрнутый в {@link CompletableFuture}.
     */
    @SuppressWarnings("UnusedReturnValue")
    CompletableFuture<ChatResponse> chatAsync(String message);

    /**
     * Отправить сообщение в чат с агентом от роли {@link ru.vych.dto.rq.chat.Role#SYSTEM}
     *
     * @param message текст сообщения
     */
    void system(String message);
}
