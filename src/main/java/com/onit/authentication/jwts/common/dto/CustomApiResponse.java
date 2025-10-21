package com.onit.authentication.jwts.common.dto;

import lombok.*;

/**
 * Standard API response wrapper.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> CustomApiResponse<T> success(T data) {
        return new CustomApiResponse<>(true, null, data);
    }

    public static <T> CustomApiResponse<T> success(T data, String message) {
        return new CustomApiResponse<>(true, message, data);
    }

    public static CustomApiResponse<?> failure(String message) {
        return new CustomApiResponse<>(false, message, null);
    }
}
