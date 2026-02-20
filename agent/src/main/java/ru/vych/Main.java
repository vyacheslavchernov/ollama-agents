package ru.vych;

import ru.vych.dto.rq.chat.ChatMessage;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var client = new OllamaClient();

        // Получение первой доступной модели в Ollama с нужными возможностями
        var model = client.getModelByName("qwen3:4b");

        var agent = new FilesAgent(client, model);

        String lastMsgUUID = null;
        for (var msg : agent.getMessages()) {
            lastMsgUUID = msg.getUuid();
            printMsg(msg);
        }

        Scanner input = new Scanner(System.in);
        String userInput;
        while (true) {
            System.out.print(">");
            userInput = input.nextLine();
            if ("/stop".equals(userInput)) break;

            agent.chat(userInput);

            var passLastMsg = false;
            for (var msg : agent.getMessages()) {
                if (msg.getUuid().equals(lastMsgUUID)) {
                    passLastMsg = true;
                    continue;
                }
                if (passLastMsg) {
                    lastMsgUUID = msg.getUuid();
                    printMsg(msg);
                }
            }
        }
    }

    private static void printMsg(ChatMessage msg) {
        System.out.printf(
                "\n\n====CHAT MESSAGE====\nROLE: %s\nTHINK: %s\nCONTENT: %s\nTOOL CALL: %s\n\n\n",
                msg.getRole(), msg.getThinking(), msg.getContent(), msg.getToolCalls()
        );
    }
}