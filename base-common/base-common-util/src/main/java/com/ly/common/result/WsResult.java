package com.ly.common.result;

import lombok.Data;

@Data
public class WsResult<T> extends Result<T> {
    // WebSocket 消息类型
    private Integer eventType;

    //时间戳
    private Long timestamp;


    public static <T> WsResult<T> build(int type, T data, int code, String message) {
        WsResult<T> result = new WsResult<>();
        result.setEventType(type);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> WsResult<T> ok(int type, T data) {
        return build(type, data, 200, "成功");
    }

    public static <T> WsResult<T> ok(int type, T data, String message) {
        return build(type, data, 200, message);
    }

    public static <T> WsResult<T> fail(int type, String message) {
        return build(type, null, 500, message);
    }

    public static <T> WsResult<T> build(int type, ResultCodeEnum resultCodeEnum) {
        WsResult<T> result = new WsResult<>();
        result.setEventType(type);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

}