package ru.vych;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.vych.dto.rq.ApiRequestDTO;
import ru.vych.dto.rq.chat.ChatRequestBody;
import ru.vych.dto.rq.embed.GenerateEmbeddingRequestBody;
import ru.vych.dto.rq.generate.GenerateRequestBody;
import ru.vych.dto.rq.model.ModelDetailsBody;
import ru.vych.dto.rq.repo.DeleteModelRequestBody;
import ru.vych.dto.rq.repo.PullModelRequestBody;
import ru.vych.dto.rs.ApiResponseDTO;
import ru.vych.dto.rs.HttpCodeResponse;
import ru.vych.dto.rs.chat.ChatResponse;
import ru.vych.dto.rs.embed.GenerateEmbeddingResponseBody;
import ru.vych.dto.rs.generate.GenerateResponseBody;
import ru.vych.dto.rs.model.Model;
import ru.vych.dto.rs.model.ModelCapabilities;
import ru.vych.dto.rs.model.ModelsList;
import ru.vych.dto.rs.repo.StatusResponseBody;
import ru.vych.dto.rs.version.Version;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static ru.vych.HttpMethods.*;

/**
 * Класс реализует методы для обращения к Ollama.
 * Может работать как с локальной установкой(по умолчанию), так и с удалённой.
 *
 * @see <a href="https://docs.ollama.com/api">API Reference</a>
 * См. список доступных методов
 */
@Slf4j
public class OllamaClient {
    public static final String DEFAULT_OLLAMA_PROTOCOL = "http";
    public static final String DEFAULT_OLLAMA_HOST = "localhost";
    public static final String DEFAULT_OLLAMA_PORT = "11434";

    private static final String OLLAMA_URL_PATTERN = "%s://%s:%s/api/";

    //region ENDPOINTS
    private static final String VERSION_ENDPOINT = "version";
    private static final String TAGS_ENDPOINT = "tags";
    private static final String PS_ENDPOINT = "ps";
    private static final String SHOW_ENDPOINT = "show";
    private static final String GENERATE_ENDPOINT = "generate";
    private static final String CHAT_ENDPOINT = "chat";
    private static final String EMBED_ENDPOINT = "embed";
    private static final String PULL_ENDPOINT = "pull";
    private static final String DELETE_ENDPOINT = "delete";
    //endregion ENDPOINTS

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final String ollamaUrl;
    private final HttpClient httpClient;

    /**
     * Создаёт клиент со значениями сервера(протокол, хост, порт) по умолчанию
     */
    public OllamaClient() {
        ollamaUrl = String.format(OLLAMA_URL_PATTERN, DEFAULT_OLLAMA_PROTOCOL, DEFAULT_OLLAMA_HOST, DEFAULT_OLLAMA_PORT);
        httpClient = HttpClient.newHttpClient();
        log.debug("Created default Ollama client for {}", ollamaUrl);
    }

    /**
     * Создаёт клиент с определёнными значениями сервера(протокол, хост, порт).
     */
    public OllamaClient(String protocol, String host, String port) {
        ollamaUrl = String.format(OLLAMA_URL_PATTERN, protocol, host, port);
        httpClient = HttpClient.newHttpClient();
        log.debug("Created Ollama client for {}", ollamaUrl);
    }

    //region VERSION_ENDPOINT

    /**
     * @return Версия Ollama
     * @see <a href="https://docs.ollama.com/api-reference/get-version">API Reference</a>
     */
    public Version version() {
        return callApi(VERSION_ENDPOINT, GET, Version.class);
    }
    //endregion

    //region TAGS_ENDPOINT

    /**
     * @return Список моделей доступных на сервере Ollama
     * @see <a href="https://docs.ollama.com/api/tags">API Reference</a>
     */
    public ModelsList availableModels() {
        return callApi(TAGS_ENDPOINT, GET, ModelsList.class);
    }
    //endregion

    //region PS_ENDPOINT

    /**
     * @return Список моделей загруженных в память
     * @see <a href="https://docs.ollama.com/api/ps">API Reference</a>
     */
    public ModelsList loadedModels() {
        return callApi(PS_ENDPOINT, GET, ModelsList.class);
    }
    //endregion

    //region SHOW_ENDPOINT

    /**
     * Получить подробную информацию о модели.
     *
     * @param model модель для которой следует запросить подробную информацию
     * @return объект модели с подробными данными
     * @see <a href="https://docs.ollama.com/api-reference/show-model-details">API Reference</a>
     */
    public Model modelDetails(Model model) {
        return modelDetails(model, false);
    }

    /**
     * Получить подробную информацию о модели.
     *
     * @param model   модель для которой следует запросить подробную информацию
     * @param verbose если true, возвращает расширенные данные, включая подробности по слоям, токенизатору и параметрам
     * @return объект модели с подробными данными
     * @see <a href="https://docs.ollama.com/api-reference/show-model-details">API Reference</a>
     */
    // TODO: https://github.com/vyacheslavchernov/ollama-client-java/issues/1 При verbose==true падает. Требуется правка в ModelDetails
    public Model modelDetails(Model model, boolean verbose) {
        return new Model().copy(model).copy(modelDetails(model.getName(), verbose));
    }

    /**
     * Получить подробную информацию о модели.
     *
     * @param name имя модели
     * @return подробные данные о модели
     * @see <a href="https://docs.ollama.com/api-reference/show-model-details">API Reference</a>
     */
    public Model modelDetails(String name) {
        return modelDetails(name, false);
    }

    /**
     * Получить подробную информацию о модели.
     *
     * @param name    имя модели
     * @param verbose если true, возвращает расширенные данные, включая подробности по слоям, токенизатору и параметрам
     * @return подробные данные о модели
     * @see <a href="https://docs.ollama.com/api-reference/show-model-details">API Reference</a>
     */
    // TODO: https://github.com/vyacheslavchernov/ollama-client-java/issues/1 При verbose==true падает. Требуется правка в ModelDetails
    public Model modelDetails(String name, boolean verbose) {
        return callApi(SHOW_ENDPOINT, POST, Model.class, new ModelDetailsBody(name, verbose));
    }
    //endregion

    //region GENERATE_ENDPOINT

    /**
     * Сгенерировать ответ по предоставленным параметрам.
     * Метод для использования с параметром stream==false. Параметр будет принудительно выставлен.
     * Для потокового получения ответа необходимо использовать перегрузку метод {@link OllamaClient#generateAsyncResponse(GenerateRequestBody)}
     *
     * @param parameters параметры генерации, включая промпт
     * @return ответ от Ollama с результатами генерации
     * @see <a href="https://docs.ollama.com/api/generate">Api Reference</a>
     */
    public GenerateResponseBody generateResponse(GenerateRequestBody parameters) {
        parameters.setStream(false);
        return callApi(GENERATE_ENDPOINT, POST, GenerateResponseBody.class, parameters);
    }

    /**
     * Сгенерировать ответ по предоставленным параметрам.
     * Метод для использования с параметром stream==true. Параметр будет принудительно выставлен.
     * Для получения ответа без потока необходимо использовать перегрузку метод {@link OllamaClient#generateResponse(GenerateRequestBody)}
     *
     * @param parameters параметры генерации, включая промпт
     * @return поток с частичным ответом генерации
     * @see <a href="https://docs.ollama.com/api/generate">Api Reference</a>
     */
    public CompletableFuture<Stream<GenerateResponseBody>> generateAsyncResponse(GenerateRequestBody parameters) {
        parameters.setStream(true);
        return callApiAsync(GENERATE_ENDPOINT, POST, GenerateResponseBody.class, parameters);
    }
    //endregion

    //region CHAT_ENDPOINT

    /**
     * Сгенерировать ответ в контексте чата (контекста).
     * Метод для использования с параметром stream==false. Параметр будет принудительно выставлен.
     * Для потокового получения ответа необходимо использовать перегрузку метод {@link OllamaClient#proceedAsyncChat(ChatRequestBody)}
     *
     * @param parameters параметры генерации, включая контекст чата
     * @return ответ сгенерированный на основе контекста чата
     * @see <a href="https://docs.ollama.com/api/chat">Api Reference</a>
     */
    public ChatResponse proceedChat(ChatRequestBody parameters) {
        parameters.setStream(false);
        return callApi(CHAT_ENDPOINT, POST, ChatResponse.class, parameters);
    }

    /**
     * Сгенерировать ответ в контексте чата (контекста).
     * Метод для использования с параметром stream==true. Параметр будет принудительно выставлен.
     * Для получения ответа без потока необходимо использовать перегрузку метод {@link OllamaClient#proceedChat(ChatRequestBody)}
     *
     * @param parameters параметры генерации, включая контекст чата
     * @return поток с частичным ответом генерации
     * @see <a href="https://docs.ollama.com/api/chat">Api Reference</a>
     */
    public CompletableFuture<Stream<ChatResponse>> proceedAsyncChat(ChatRequestBody parameters) {
        parameters.setStream(true);
        return callApiAsync(CHAT_ENDPOINT, POST, ChatResponse.class, parameters);
    }
    //endregion

    //region EMBED_ENDPOINT

    /**
     * Сгенерировать эмбеддинги с помощью модели
     *
     * @param parameters параметры генерации, включая текст для генерации эмбеддингов
     * @return ответ генерации
     * @see <a href="https://docs.ollama.com/api/embed">Api Reference</a>
     */
    public GenerateEmbeddingResponseBody generateEmbedding(GenerateEmbeddingRequestBody parameters) {
        return callApi(EMBED_ENDPOINT, POST, GenerateEmbeddingResponseBody.class, parameters);
    }
    //endregion

    //region model finders

    /**
     * Получить модели доступные в Ollama отобранные по их возможностям
     * Если моделей не найдено, то возвращается пустой список.
     *
     * @param capabilities множество возможностей по которым должен произойти отбор
     * @return список моделей подходящий под условия
     */
    public List<Model> getModelsByCapabilities(Set<ModelCapabilities> capabilities) {
        return availableModels().getModels().stream()
                .filter(model -> {
                    var details = modelDetails(model);
                    return new HashSet<>(details.getCapabilities()).containsAll(capabilities);
                }).toList();
    }

    /**
     * Получить модели доступные в Ollama отобранные по их семейству.
     * Если моделей не найдено, то возвращается пустой список.
     *
     * @param modelFamily семейство по которому будет произведён отбор
     * @return список моделей подходящих под условия
     */
    public List<Model> getModelsByFamily(String modelFamily) {
        return availableModels().getModels().stream()
                .filter(model -> model.getDetails().getFamily().toLowerCase().trim().contains(modelFamily.toLowerCase().trim())
                        || Arrays.stream(model.getDetails().getFamilies())
                        .anyMatch(f -> f.toLowerCase().trim().contains(modelFamily.toLowerCase().trim()))).toList();
    }

    /**
     * Получить конкретную модель доступную в Ollama по её имени.
     * Если модель не найдена, то возвращается null.
     *
     * @param modelName имя модели для поиска
     * @return объект модели или null, если модель не найдена
     */
    public Model getModelByName(String modelName) {
        return availableModels().getModels().stream()
                .filter(model -> modelName.toLowerCase().trim().equals(model.getName().toLowerCase().trim()))
                .findFirst()
                .orElse(null);
    }
    //endregion

    //region REPO ENDPOINTS

    /**
     * Загрузить модель в локальный репозиторий Ollama.
     * Метод для использования с параметром stream==false. Параметр будет принудительно выставлен.
     * Для потокового получения ответа необходимо использовать перегрузку метод {@link OllamaClient#pullModelAsync(PullModelRequestBody)}
     *
     * @param parameters параметры запроса
     * @return статус загрузки
     * @see <a href="https://docs.ollama.com/api/pull">API Reference</a>
     */
    public StatusResponseBody pullModel(PullModelRequestBody parameters) {
        parameters.setStream(false);
        return callApi(PULL_ENDPOINT, POST, StatusResponseBody.class, parameters);
    }

    /**
     * Загрузить модель в локальный репозиторий Ollama.
     * Метод для использования с параметром stream==true. Параметр будет принудительно выставлен.
     * Для не потокового получения ответа необходимо использовать перегрузку метод {@link OllamaClient#pullModel(PullModelRequestBody)}
     *
     * @param parameters параметры запроса
     * @return поток с промежуточными статусами загрузки
     * @see <a href="https://docs.ollama.com/api/pull">API Reference</a>
     */
    public CompletableFuture<Stream<StatusResponseBody>> pullModelAsync(PullModelRequestBody parameters) {
        parameters.setStream(true);
        return callApiAsync(PULL_ENDPOINT, POST, StatusResponseBody.class, parameters);
    }

    /**
     * Удалить модель из локального репозитория Ollama
     *
     * @param parameters параметры запроса
     * @return статус-код ответа (200 - удаление успешно)
     * @see <a href="https://docs.ollama.com/api/delete">API Reference</a>
     */
    public HttpCodeResponse deleteModel(DeleteModelRequestBody parameters) {
        return callApi(DELETE_ENDPOINT, DELETE, HttpCodeResponse.class, parameters);
    }
    //endregion

    /**
     * Вызов API Ollama
     *
     * @param endpoint    вызываемый эндпоинт API
     * @param method      HTTP метод вызова
     * @param responseDto класс DTO в который будет помещён ответ от API
     * @param payload     нагрузка запроса к API
     * @param <T>         класс DTO в который будет помещён ответ от API
     * @return ответ от API Ollama
     */
    @SneakyThrows
    private <T extends ApiResponseDTO> T callApi(String endpoint, HttpMethods method, Class<T> responseDto, ApiRequestDTO... payload) {
        var uri = new URI(ollamaUrl + endpoint);
        var rq = buildRequest(uri, method, false, payload);
        var rs = httpClient.send(rq, HttpResponse.BodyHandlers.ofString());

        if (responseDto.equals(HttpCodeResponse.class)) {
            log.debug("Got response from [{}] with code {}", uri, rs.statusCode());
            //noinspection unchecked
            return (T) new HttpCodeResponse(rs.statusCode());
        }

        T rsDto = mapper.readValue(rs.body(), responseDto);
        log.debug("Got response from [{}] with code {} : {}", uri, rs.statusCode(), mapper.writeValueAsString(rsDto));
        return rsDto;
    }

    /**
     * Асинхронный вызов API Ollama
     *
     * @param endpoint    вызываемый эндпоинт API
     * @param method      HTTP метод вызова
     * @param responseDto класс DTO в который будет помещён ответ от API
     * @param payload     нагрузка запроса к API
     * @param <T>         класс DTO в который будет помещён ответ от API
     * @return {@link Stream} ответов от API Ollama в виде {@link CompletableFuture}
     */
    @SneakyThrows
    private <T extends ApiResponseDTO> CompletableFuture<Stream<T>> callApiAsync(String endpoint, HttpMethods method, Class<T> responseDto, ApiRequestDTO... payload) {
        var uri = new URI(ollamaUrl + endpoint);
        var rq = buildRequest(uri, method, true, payload);
        return httpClient.sendAsync(rq, HttpResponse.BodyHandlers.ofLines())
                .thenApply(rs ->
                        rs.body()
                                .filter(line -> !line.isBlank())
                                .map(line -> {
                                    try {
                                        T rsDto = mapper.readValue(line, responseDto);
                                        log.debug("Got partial response from [{}] : {}", uri, mapper.writeValueAsString(rsDto));
                                        return rsDto;
                                    } catch (Exception e) {
                                        throw new RuntimeException("Failed to parse line: " + line, e);
                                    }
                                })
                );
    }

    /**
     * Собрать объект запроса к API Ollama
     *
     * @param uri     путь запроса
     * @param method  метод запроса
     * @param async   признак асинхронности запроса
     * @param payload тело запроса
     * @return сформированный, на основе переданных данных, объект запроса
     */
    @SneakyThrows
    private HttpRequest buildRequest(URI uri, HttpMethods method, boolean async, ApiRequestDTO... payload) {
        var rqBody = payload.length == 0 ? "" : mapper.writeValueAsString(payload[0]);
        log.debug("Sending {} {} request on [{}] with payload: {}", async ? "async" : "", method, uri, rqBody);
        var rq = HttpRequest.newBuilder(uri);
        switch (method) {
            case GET -> rq.GET();
            case POST -> rq.POST(HttpRequest.BodyPublishers.ofString(rqBody));
            case DELETE -> rq.method("DELETE", HttpRequest.BodyPublishers.ofString(rqBody));
            default -> throw new RuntimeException("Unexpected http method - " + method);
        }
        return rq.build();
    }
}
