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

public class MainCLI {
    // todo: подумать над доп. тулзами
    // todo: сохранение истории чатов
    // todo: сжатие контекста
    // todo: суб-агенты


    @SneakyThrows
    public static void main(String[] args) {
        var client = new OllamaClient();
        ToolRegistry.scanResourcesForConfigs("config/tools");

        var agent = new ConfigurableAgent(client, AgentConfig.loadFromFile(
                "config/agents/chat/config.yaml",
                PathType.RELATIVE_TO_RESOURCES
        ));
        setCallbacks(agent);

        for (var msg : agent.getMessages()) {
            printMsg(msg);
        }

        Scanner input = new Scanner(System.in);
        String userInput;
        var running = true;
        while (running) {
            if (!agent.isWorking()) {
                System.out.print(">");
                userInput = input.nextLine();

                var inputSplit = userInput.split(" ", 3);
                switch (inputSplit[0]) {
                    case "/help":
                        System.out.println(CommandsCLI.getHelp());
                        continue;

                    case "/stop":
                        running = false;
                        break;

                    case "/clear":
                        agent.clearMessages();
                        System.out.println("Context cleared.");
                        continue;

                    case "/agent":
                        if (inputSplit.length > 2 && "switch".equals(inputSplit[1])) {
                            System.out.println("Switching agent to `" + inputSplit[2] + "`");
                            agent = new ConfigurableAgent(client, AgentConfig.loadFromFile(
                                    "config/agents/" + inputSplit[2] + "/config.yaml",
                                    PathType.RELATIVE_TO_RESOURCES
                            ));
                            setCallbacks(agent);
                        } else {
                            if (inputSplit.length == 2) {
                                switch (inputSplit[1]) {
                                    case "current":
                                        System.out.printf(
                                                "Current agent `%s` is loaded from `%s`. Agent description: `%s`\n",
                                                agent.getConfig().getName(),
                                                agent.getConfig().getConfigPath(),
                                                agent.getConfig().getDescription()
                                        );
                                        break;
                                    case "reload":
                                        System.out.printf(
                                                "Reloading agent `%s` config from path `%s`...\n",
                                                agent.getConfig().getName(),
                                                agent.getConfig().getConfigPath()
                                        );
                                        agent = new ConfigurableAgent(client, AgentConfig.loadFromFile(
                                                agent.getConfig().getConfigPath(),
                                                PathType.RELATIVE_TO_RESOURCES
                                        ));
                                        setCallbacks(agent);
                                        System.out.println("Config reloaded\n");
                                        break;
                                }
                            } else {
                                System.out.println("Unknown command. Try /help");
                            }

                        }
                        continue;
                }

                agent.chatAsync(userInput);
            } else {
                //noinspection BusyWait
                Thread.sleep(500);
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

    private static void setCallbacks(ConfigurableAgent agent) {
        //        agent.setMessagesUpdateCallback(Main::printMsg);
        agent.setAsyncChatResponseGenerationCallback((chatResponse, generationStep) -> {
            switch (generationStep) {
                case STARTED -> System.out.print("\nРазмышления модели:\n" + chatResponse.getMessage().getThinking());
                case THINKING -> System.out.print(Objects.requireNonNullElse(
                        chatResponse.getMessage().getThinking(),
                        "\nОтвет модели:\n" + chatResponse.getMessage().getContent()
                ));
                case GENERATING_RESPONSE -> System.out.print(chatResponse.getMessage().getContent());
                case DONE -> System.out.print("\n\n");
            }
        });

        agent.setToolInvokeCallback((tool, result) ->
                System.out.printf("\nTool called: %s\nResult: %s\n\n", tool.getFunction(), result));
    }
}