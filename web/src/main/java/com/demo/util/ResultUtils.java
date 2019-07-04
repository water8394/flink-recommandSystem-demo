package com.demo.util;

public class ResultUtils {

    public static Result success() {
        return new Result<>(ResultCode.SUCCESS, null);
    }

    public static Result success(Object data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    public static Result fail(ResultCode resultCode, String msg) {
        Result<Object> result = new Result<>(resultCode);
        result.setMsg(msg);
        return result;
    }

    public static Result fail(ResultCode resultCode) {
        return new Result(resultCode);
    }
}
