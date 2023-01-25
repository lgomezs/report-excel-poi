package com.example.demopoi.exception;

public class ErrorApplication extends RuntimeException{
    private Integer httpStatusCode;

    private Integer code;

    private String title;

    private String detail;
    

    public ErrorApplication(final Integer code, final String title, final String detail) {
        this.httpStatusCode = code;
        this.code = code;
        this.title = title;
        this.detail = detail;
    }
}
