package com.financas.projeto.common.response;

import java.time.LocalDateTime;

public class ApiError {
    private boolean success;
    private String error;
    private int status;
    private LocalDateTime timestamp;

    public ApiError(String error, int status) {
        this.success = false;
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public boolean getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
