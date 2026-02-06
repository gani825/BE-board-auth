package com.green.boardauth.application.user;

import com.green.boardauth.application.user.model.UserGetOneRes;
import com.green.boardauth.application.user.model.UserSignInReq;
import com.green.boardauth.application.user.model.UserSignInRes;
import com.green.boardauth.application.user.model.UserSignUpReq;
import com.green.boardauth.configuration.model.JwtUser;
import com.green.boardauth.configuration.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 로그 출력을 위한 Lombok 어노테이션
@Slf4j

// 이 클래스가 비즈니스 로직(Service 계층)임을 스프링에게 알림
@Service

// final 필드를 매개변수로 받는 생성자를 자동 생성
// → 생성자 주입(DI)을 위한 어노테이션
@RequiredArgsConstructor
public class UserService {

    // MyBatis Mapper (DB 접근 담당)
    // final → 스프링이 생성자를 통해 주입
    private final UserMapper userMapper;

    // 비밀번호 암호화 / 비교를 담당하는 객체
    // SecurityConfig에서 Bean으로 등록된 PasswordEncoder가 주입됨
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리
    public int signUp(UserSignUpReq req) {

        // 사용자가 입력한 평문 비밀번호를 암호화
        String hashedPw = passwordEncoder.encode(req.getUpw());

        // 암호화된 비밀번호 로그 출력 (확인용)
        log.info("hashedPw : {}", hashedPw);

        // 요청 객체에 암호화된 비밀번호 다시 세팅
        req.setUpw(hashedPw);

        // Mapper를 통해 DB에 회원 정보 저장
        return userMapper.signUp(req);
    }

    // 로그인 처리
    public UserSignInRes signIn(UserSignInReq req) {

        // 아이디(uid)를 기준으로 회원 정보 조회
        UserGetOneRes res = userMapper.findByUid(req.getUid());

        // DB에서 가져온 회원 정보 로그 출력
        log.info("res : {}", res);

        // 사용자가 입력한 비밀번호와
        // DB에 저장된 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(req.getUpw(), res.getUpw())) {
            // 비밀번호가 일치하지 않으면 실패
            return null;
        }
        // 비밀번호가 일치하면 로그인 성공
        // 예전에는 AT, RT을 FE전달 >>> 보안 쿠키 이용

        return UserSignInRes.builder()
                .signedUserId(res.getId())
                .nm(res.getNm())
                .build();
    }
}
