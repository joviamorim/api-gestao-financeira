package com.financas.projeto.common.response;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(true, data, message);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(false, null, message);
    }

    public boolean getSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
