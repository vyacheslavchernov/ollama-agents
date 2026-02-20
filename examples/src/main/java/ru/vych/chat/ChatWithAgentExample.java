package ru.vych.chat;

import ru.vych.OllamaClient;

import java.util.Set;

import static ru.vych.dto.rs.model.ModelCapabilities.COMPLETION;
import static ru.vych.dto.rs.model.ModelCapabilities.TOOLS;

/**
 * Пример обмена сообщениями с моделью с удержанием контекста (чата).
 * Для более детальной информации по агенту см. {@link BankAgent}
 */
public class ChatWithAgentExample {
    public static void main(String[] args) {
        // Создание клиента
        var client = new OllamaClient();

        // Получение первой доступной модели в Ollama с нужными возможностями
        var model = client.getModelsByCapabilities(Set.of(TOOLS, COMPLETION)).getFirst();

        // Создание кастомного агента
        var bankAgent = new BankAgent(client, model);

        // Отправляем запросы в чат с агентом
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
