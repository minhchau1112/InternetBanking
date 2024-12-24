package com.example.backend.dto.response;

import com.example.backend.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private T data;
    private String message;
    private LocalDateTime responseTime;

    public ApiResponse(boolean success, int code, T data, String message, LocalDateTime responseTime) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.message = message;
        this.responseTime = responseTime;
    }
}
