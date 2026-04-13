package ru.vych.tools.web;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация провайдера поиска DuckDuckGO
 */
public class DuckDuckGoSearch implements SearchProvider {

    @Override
    @SneakyThrows
    public List<SearchResult> search(String query, Integer maxResults) {
        String url = "https://html.duckduckgo.com/html/?q=" +
                URLEncoder.encode(query, StandardCharsets.UTF_8);

        Document doc = PlaywrightClient.getInstance().get(url, Map.of(
                "Accept", "text/html,application/xhtml+xml",
                "Accept-Language", "en-US,en;q=0.9",
                "Connection", "keep-alive",
                "Referer", "https://duckduckgo.com/"
        ));

        List<SearchResult> results = new ArrayList<>();

        Elements elements = doc.select(".result");

        for (Element el : elements) {
            if (results.size() >= maxResults) break;

            Element titleEl = el.selectFirst(".result__title a");
            Element snippetEl = el.selectFirst(".result__snippet");

            if (titleEl == null) continue;

            String title = titleEl.text();
            String link = titleEl.attr("href");
            String snippet = snippetEl != null ? snippetEl.text() : "";

            // простая очистка / обрезка
            if (snippet.length() > 200) {
                snippet = snippet.substring(0, 200) + "...";
            }

            results.add(new SearchResult(title, link, snippet));
        }

        return results;
    }
}
