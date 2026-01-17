package com.passkey;

public class Result<T> {
    private final int rowsAffected;
    private final boolean success;
    private final String message;
    private final T data;

    public Result(int rowsAffected, boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.rowsAffected = rowsAffected;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getRowsAffected() { return rowsAffected; }
    public T getData() { return data; }
}
