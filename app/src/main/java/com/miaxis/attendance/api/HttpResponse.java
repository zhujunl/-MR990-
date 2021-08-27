package com.miaxis.attendance.api;


public class HttpResponse<T> {
    public String code;
    public String message;
    public T result;

    public HttpResponse() {
    }

    public HttpResponse(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess(){
        return "200".equals(this.code);
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
