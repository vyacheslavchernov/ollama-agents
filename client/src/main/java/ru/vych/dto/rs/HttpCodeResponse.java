package ru.vych.dto.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для ответа, который содержит только статус-код
 */
@Getter
@Setter
@AllArgsConstructor
public class HttpCodeResponse implements ApiResponseDTO {
    /**
     * Статус-код ответа
     */
    private int code;
}
