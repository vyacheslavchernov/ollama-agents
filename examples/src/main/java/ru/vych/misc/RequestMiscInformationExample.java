package ru.vych.misc;

import ru.vych.OllamaClient;

/**
 * Пример запроса дополнительной информации о Ollama
 */
public class RequestMiscInformationExample {
    public static void main(String[] args) {
        // Создание клиента
        var client = new OllamaClient();

        // Получение версии Ollama к которой подключен клиент.
        // Можно использовать для проверки доступности Ollama.
        var version = client.version();
        System.out.println("Current Ollama version is " + version.getVersion());
    }
}
