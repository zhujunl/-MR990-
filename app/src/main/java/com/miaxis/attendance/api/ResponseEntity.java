package com.miaxis.attendance.api;


public class ResponseEntity<T> {
    private String code;
    private String message;
    private T data;

    public ResponseEntity() {
    }

    public ResponseEntity(String code, String message, T data) {
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
