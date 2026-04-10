package com.financas.projeto.common.response;

import java.time.LocalDateTime;

public class ApiError {
    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ApiError(String message, int status) {
        this.success = false;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
