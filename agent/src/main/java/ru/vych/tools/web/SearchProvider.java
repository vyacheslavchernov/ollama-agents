package ru.vych.tools.web;

import java.util.List;

/**
 * Интерфейс провайдера поиска в интернете
 */
public interface SearchProvider {
    /**
     * Произвести поиск в интернете
     *
     * @param query поисковый запрос
     * @param maxResults количество результатов, которые следует вернуть
     * @return список результатов поиска
     */
    List<SearchResult> search(String query, Integer maxResults);
}
