package com.green.boardauth.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 모든 필드에 대한 getter 메서드를 자동 생성
// resultMessage(), resultData() 형태로 접근 가능
@Getter

// 모든 필드를 매개변수로 받는 생성자 자동 생성
// new ResultResponse<>(message, data) 형태로 바로 사용 가능
@AllArgsConstructor
public class ResultResponse<T> {

    // 요청 처리 결과에 대한 설명 메시지
    // 예: "회원가입 성공", "로그인 실패"
    private String resultMessage;

    // 실제 응답 데이터
    // 제네릭(T)을 사용해 어떤 타입의 데이터든 담을 수 있게 설계
    // 예: Integer, List, Object 등
    private T resultData;
}
