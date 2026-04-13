package ru.vych.tools.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO с обработанным контентом HTML страницы
 */
@Getter
@Setter
@AllArgsConstructor
public class PageResult {
    public String url;
    public String title;
    public String content;
}
