package ru.vych;

import lombok.SneakyThrows;
import ru.vych.agent.ConfigurableAgent;
import ru.vych.agent.config.PathType;
import ru.vych.agent.config.agent.AgentConfig;
import ru.vych.agent.tools.ToolRegistry;
import ru.vych.dto.rq.chat.ChatMessage;
import ru.vych.dto.rq.chat.Role;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    // todo: правка ответов в тулзах
    // todo: нормальное покрытие логами
    // todo: нормальные текстовки ошибок
    // todo: webfetch
    // todo: подумать над доп. тулзами


    @SneakyThrows
    public static void main(String[] args) {
        var client = new OllamaClient();
        ToolRegistry.scanResourcesForConfigs("config/tools");

        var agent = new ConfigurableAgent(
                client,
                AgentConfig.loadFromFile("config/agents/html_dev/config.yaml", PathType.RELATIVE_TO_RESOURCES)
        );
//        agent.setMessagesUpdateCallback(Main::printMsg);
        agent.setAsyncChatResponseGenerationCallback((chatResponse, generationStep) -> {
            switch (generationStep) {
                case STARTED -> System.out.print("\nРазмышления модели:\n" + chatResponse.getMessage().getThinking());
                case THINKING -> System.out.print(Objects.requireNonNullElse(
                        chatResponse.getMessage().getThinking(),
                        "\nОтвет модели:\n"
                ));
                case GENERATING_RESPONSE -> System.out.print(chatResponse.getMessage().getContent());
                case DONE -> System.out.print("\n\n");
            }
        });

        agent.setToolInvokeCallback((tool, result) ->
                System.out.printf("\nTool called: %s\nResult: %s\n\n", tool.getFunction(), result));

        for (var msg : agent.getMessages()) {
            printMsg(msg);
        }

        Scanner input = new Scanner(System.in);
        String userInput;
        while (true) {
            if (!agent.isWorking()) {
                System.out.print(">");
                userInput = input.nextLine();
                if ("/stop".equals(userInput)) break;

                agent.chatAsync(userInput);
            } else {
                //noinspection BusyWait
                Thread.sleep(250);
            }
        }
    }

    private static void printMsg(ChatMessage msg) {
        if (msg.getRole() != Role.USER) {
            System.out.printf(
                    "\n\n====CHAT MESSAGE====\nROLE: %s\nTHINK: %s\nCONTENT: %s\nTOOL CALL: %s\n\n\n",
                    msg.getRole(), msg.getThinking(), msg.getContent(), msg.getToolCalls()
            );
        }
    }
}