package com.demo.util;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result() {}

    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }
    public Result(ResultCode resultCode, T data) {
        this(resultCode);
        this.data = data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
