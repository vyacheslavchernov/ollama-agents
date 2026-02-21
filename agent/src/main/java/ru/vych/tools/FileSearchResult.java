package ru.vych.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSearchResult {
    private String filePath;
    private String occurredErrorMessage;
}
