package com.miaxis.attendance.api;


public class HttpResponse<T> {
    public String code;
    public String message;
    public T data;

    public HttpResponse() {
    }

    public HttpResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
