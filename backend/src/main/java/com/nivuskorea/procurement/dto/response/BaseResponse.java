package com.nivuskorea.procurement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> {
    private int code; // 상태 코드
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(200, "success", data);
    }

    public static BaseResponse<Void> error(int code, String message) {
        return new BaseResponse<Void>(code, message, null);
    }
}
