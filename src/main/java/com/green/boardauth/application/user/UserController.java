package com.green.boardauth.application.user;

import com.green.boardauth.application.user.model.UserSignInReq;
import com.green.boardauth.application.user.model.UserSignUpReq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 클래스는 REST API 컨트롤러
// HTML 화면을 반환하지 않고 JSON 데이터를 응답으로 내려준다
@RestController

// final 필드를 매개변수로 하는 생성자를 자동 생성
// → 생성자 주입(DI)을 위해 사용
@RequiredArgsConstructor

// 로그 출력을 위한 Lombok 어노테이션
// log.info(), log.error() 등을 바로 사용할 수 있음
@Slf4j

// 이 컨트롤러의 공통 URL
// 모든 요청은 /api/user 로 시작
@RequestMapping("/api/user")
public class UserController {

    // 비즈니스 로직을 처리하는 Service
    // final → 생성자 주입
    private final UserService userService;

    // 회원가입 API
    // POST /api/user/signup
    @PostMapping("/signup")
    public int signup(@RequestBody UserSignUpReq req) {

        // 클라이언트에서 넘어온 회원가입 요청 데이터 로그 출력
        log.info("req : {}", req);

        // Service에 회원가입 처리 위임
        int result = userService.signUp(req);

        // 처리 결과를 그대로 응답
        return result;
    }

    // 로그인 API
    // POST /api/user/signin
    @PostMapping("/signin")
    public int signIn(@RequestBody UserSignInReq req) {

        // 클라이언트에서 넘어온 로그인 요청 데이터 로그 출력
        log.info("req : {}", req);

        // Service에 로그인 처리 위임
        int result = userService.signIn(req);

        // 로그인 성공 / 실패 결과 반환
        return result;
    }
}
