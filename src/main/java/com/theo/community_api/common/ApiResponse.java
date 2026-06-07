package com.theo.community_api.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> of (String message, T data){
        return new ApiResponse<>(message, data);
    }

    public static ApiResponse<Void> of(String message) {
        return new ApiResponse<>(message, null);
    }
}
