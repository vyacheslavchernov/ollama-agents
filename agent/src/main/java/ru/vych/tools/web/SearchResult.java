package ru.vych.tools.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для результата поиска в интернете
 */
@Getter
@Setter
@AllArgsConstructor
public class SearchResult {
    public String title;
    public String link;
    public String snippet;
}
