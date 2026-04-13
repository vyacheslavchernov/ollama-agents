package ru.vych.tools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.vych.agent.tools.ToolJsonResponseWrapper;
import ru.vych.tools.web.DuckDuckGoSearch;
import ru.vych.tools.web.PageResult;
import ru.vych.tools.web.SearchProvider;
import ru.vych.tools.web.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.vych.tools.web.UserAgentProvider.getRandomUserAgent;

/**
 * Набор методов, которые используются агентами
 * как инструменты для работы с интернетом (поиск, получения страниц итд.)
 */
@Slf4j
public class WebTools {
    /**
     * Список провайдеров для поиска в интернете
     */
    private static final List<SearchProvider> SEARCH_PROVIDERS = List.of(
            new DuckDuckGoSearch()
    );

    /**
     * Осуществить поиск по запросу с помощью доступных провайдеров
     *
     * @param query      поисковой запрос
     * @param maxResults максимальное количество результатов в выдаче
     * @return maxResults результатов поиска
     */
    public static String webSearch(String query, Integer maxResults) {
        var response = new ToolJsonResponseWrapper("web_search", Map.of(
                "query", query,
                "maxResults", maxResults
        ));

        List<SearchResult> results = new ArrayList<>();
        for (SearchProvider provider : SEARCH_PROVIDERS) {
            results.addAll(provider.search(query, maxResults));
            if (!results.isEmpty()) {
                break;
            }
        }

        if (results.isEmpty()) {
            response.addContent("error", "Не получено ни одного результата поиска. " +
                    "Скорее всего провайдеры поиска недоступны.");
        } else {
            response.addContent("results", results);
        }

        return response.json();
    }

    /**
     * Получить очищенный от мусора контент страницы в интернете
     *
     * @param url      адрес страницы
     * @param maxChars максимальная длина контента
     * @return контент страницы очищенный от мусора и обрезанный до необходимой длинны
     */
    public static String fetchPage(String url, Integer maxChars) {
        var response = new ToolJsonResponseWrapper("fetch_page", Map.of(
                "url", url,
                "maxChars", maxChars
        ));

        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent(getRandomUserAgent())
                    .timeout(100_000)
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://duckduckgo.com/")
                    .get();
        } catch (IOException e) {
            log.error("Got exception while fetching page", e);
            return response.addError(e).json();
        }

        doc.select("script, style, noscript, header, footer, nav, aside").remove();
        String title = doc.title();
        String text = extractMainText(doc);
        text = text.replaceAll("\\s+", " ").trim();

        if (text.length() > maxChars) {
            text = text.substring(0, maxChars) + "...";
        }

        return response.addContent("content", new PageResult(url, title, text)).json();
    }

    /**
     * Извлечь основной текст из страницы
     *
     * @param doc страницы
     * @return основной текст страницы очищенный от мусора
     */
    private static String extractMainText(Document doc) {
        Element article = doc.selectFirst("article");
        if (article != null) return article.text();

        Element main = doc.selectFirst("main");
        if (main != null) return main.text();

        return doc.body().text();
    }
}
