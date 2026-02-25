package ru.vych.tools.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Результаты поиска.
 * Содержит абсолютный путь до файла\каталога, если удовлетворены условия поиска
 * или ошибку, которая произошла во время обработки файла\каталога.
 */
@Getter
@AllArgsConstructor
public class FileSearchResult {
    private String filePath;
    private Exception occurredException;
}
