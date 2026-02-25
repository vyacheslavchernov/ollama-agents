package ru.vych.agent.tools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vych.agent.config.PathType;
import ru.vych.agent.config.tools.ToolConfig;
import ru.vych.agent.config.tools.ToolParameterConfig;
import ru.vych.agent.utils.ConfigUtils;
import ru.vych.dto.rq.chat.tool.ToolDefinition;
import ru.vych.dto.rq.chat.tool.ToolFunction;
import ru.vych.dto.rq.chat.tool.ToolParameters;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Реестр инструментов для агентов.
 * Инструменты готовы к использованию при получении через методы `get`.
 */
@Slf4j
public class ToolRegistry {
    private static final Map<String, ToolDefinition> REGISTRY = new HashMap<>();
    private static final Pattern yamlPattern = Pattern.compile(".yaml$");

    /**
     * Найти и загрузить в реестр все инструменты, которые будут найдены в каталоге
     *
     * @param directory каталог для поиска
     */
    @SneakyThrows
    public static void scanResourcesForConfigs(String directory) {
        log.debug("Scanning directory `{}` in resources for tool configs", directory);
        var absolutePath = ConfigUtils.class.getClassLoader().getResource(directory);
        if (absolutePath == null) {
            throw new IOException("Directory not found: " + directory);
        }

        Path directoryPath = Paths.get(absolutePath.toURI());

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            directoryStream.forEach(entry -> {
                Matcher matcher = yamlPattern.matcher(entry.getFileName().toString());
                if (matcher.find()) {
                    log.debug("Founded tool config `{}`", entry.toAbsolutePath());
                    loadToRegistry(
                            ToolConfig.loadFromFile(
                                    entry.toAbsolutePath().toString(),
                                    PathType.ABSOLUTE
                            )
                    );
                }
            });
        }
    }

    /**
     * Создать экземпляр инструмента на основе конфига и загрузить инструмент в реестр
     *
     * @param config конфигурация инструмента
     */
    @SneakyThrows
    public static void loadToRegistry(ToolConfig config) {
        log.debug("Register tool config to registry | {}", config);
        // Сборка пропов инструмента
        // TODO: нужно(?) переписать классы пропов, чтобы они нормально описывали json schema
        boolean hasProps = config.getToolParameterConfigs() != null;
        ToolParameters toolParameters = new ToolParameters();
        List<Class<?>> parameterTypes = new LinkedList<>();

        if (hasProps) {
            for (ToolParameterConfig prop : config.getToolParameterConfigs()) {
                toolParameters.addProperty(prop.getName(), prop.getType(), prop.getDescription());
                switch (prop.getType()) {
                    case "string":
                        parameterTypes.add(String.class);
                        break;
                    case "boolean":
                        parameterTypes.add(Boolean.class);
                        break;
                    default:
                        throw new ConfigurationException("Wrong tool parameter type: Can't use " + prop.getType());
                }
            }
        }

        // Создание определения
        var def = new ToolDefinition(new ToolFunction(
                config.getName(),
                hasProps ? toolParameters : null,
                params -> {
                    // Функциональная часть инструмента
                    var methodSplit = config.getMethod().split("#");

                    // Поиск класса в котором находится метод инструмента
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(methodSplit[0]);
                    } catch (ClassNotFoundException e) {
                        log.error("Got exception reflection", e);
                        throw new RuntimeException(e);
                    }

                    // Поиск метода
                    Method method;
                    try {
                        if (hasProps) {
                            method = clazz.getDeclaredMethod(methodSplit[1], parameterTypes.toArray(Class[]::new));
                        } else {
                            method = clazz.getDeclaredMethod(methodSplit[1]);
                        }
                    } catch (NoSuchMethodException e) {
                        log.error("Got exception reflection", e);
                        throw new RuntimeException(e);
                    }

                    // Вызов метода инструмента с нужными параметрами
                    try {
                        List<Object> invokeParams = new LinkedList<>();
                        if (hasProps) {
                            for (var toolParam : config.getToolParameterConfigs()) {
                                invokeParams.add(params.get(toolParam.getName()));
                            }
                        }

                        return method.invoke(null, invokeParams.toArray()).toString();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("Got exception reflection", e);
                        throw new RuntimeException(e);
                    }
                }
        ).setDescription(config.getDescription()));

        REGISTRY.put(config.getAlias(), def);
    }

    /**
     * Получить определение инструмента по псевдониму в реестре
     *
     * @param alias псевдоним инструмента
     * @return определение инструмента
     * @throws IllegalArgumentException если инструмент не найден в реестре
     */
    @SuppressWarnings("unused")
    public static ToolDefinition getTool(String alias) {
        if (!REGISTRY.containsKey(alias)) {
            throw new IllegalArgumentException("No such tool in registry: " + alias);
        }
        return REGISTRY.get(alias);
    }

    /**
     * Получить список определений инструментов по их псевдонимам в реестре
     *
     * @param alias псевдонимы инструментов
     * @return список определений инструментов
     * @throws IllegalArgumentException если хотя бы один инструмент не найден в реестре
     */
    public static List<ToolDefinition> getToolsList(String... alias) {
        var list = new ArrayList<ToolDefinition>();
        for (var name : alias) {
            if (!REGISTRY.containsKey(name)) {
                throw new IllegalArgumentException("No such tool in registry: " + name);
            }
            list.add(REGISTRY.get(name));
        }
        return list;
    }
}
