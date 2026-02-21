package ru.vych.agent.config.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс описания конфигурации параметра инструмента
 */
@Getter
@AllArgsConstructor
public class ToolParameterConfig {
    private String name;
    private String type;
    private String description;
}
