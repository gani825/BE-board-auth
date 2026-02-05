package com.green.boardauth.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자
public class ResultResponse<T> {
    private String resultMessage;
    private T resultData;
}
