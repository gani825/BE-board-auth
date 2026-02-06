package com.green.boardauth.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 객체(Bean)를 직접 등록하는 용도
// @Configuration 애노테이션 아래에 있는 @Bean은 무조건 싱글톤이다.
// 스프링이 이 클래스를 읽어서 컨테이너 설정으로 사용함
@Configuration

@RequiredArgsConstructor
// 스프링 시큐리티 관련 설정을 적는 클래스
// → "보안은 이렇게 동작해" 라고 스프링에게 알려주는 역할
public class WebSecurityConfiguration {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    // 메서드 호출로 리턴한 값의 객체를 빈등록하게 된다.
    // 스프링이 직접 관리하도록 등록함
    // → 스프링 시큐리티가 이 설정을 자동으로 사용하게 됨
    @Bean

    // 보안 필터 묶음(SecurityFilterChain)을 만드는 메서드
    // → 요청이 들어올 때 "이 규칙대로 검사해" 라고 알려주는 설정
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // 스프링이 미리 만들어 둔 보안 설정 도구
        // → 여기다 하나씩 규칙을 추가하는 방식
        return httpSecurity

                // 로그인 상태(세션)를 어떻게 관리할지 정하는 부분
                .sessionManagement(session ->

                        // 서버가 로그인 상태를 "기억하지 않겠다"는 설정
                        // → 시큐리티에서 세션 사용 X
                        // → 요청이 올 때마다 토큰으로 확인하는 방식
                        // STATELESS : SessionCreationPolicy라는 타입에 소속된 상수값. 객체화를 안했기 때문에 static이다.
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // disable()는 “이 기능을 끈다(사용 안 한다)”는 뜻의 메서드
                .httpBasic(hb -> hb.disable()) // 시큐리티에서 제공해주는 로그이니 화면이 있는데 사용하지 않겠다.
                .formLogin(fl -> fl.disable()) // 어차피 백엔드가 화면을 만들지 않기 때문에 폼로그인도 사용하지 않겠다/
                .csrf(csrf -> csrf.disable()) // 어차피 백엔드가 화면을 만들지 않으면 csrf 공격이 의미가 없기 때문에 비활성화하겠다.

                // 인증 / 인가처리 (권한처리)

                // 아래 내용은 (POST) /api/board로 요청이 올 때는 반드시 로그인이 되어 있어야 한다.
                .authorizeHttpRequests(req -> req.requestMatchers(HttpMethod.POST, "/api/auth/signin").authenticated()
                        .anyRequest().permitAll() // 나머지 요청에 대해서는 허용하겠다.
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 위에서 적은 설정들을 실제로 적용 가능한 형태로 완성
                .build();

    }

    @Bean
    // 비밀번호를 암호화하는 도구(PasswordEncoder)를 스프링에 등록하기 위한 메서드
    public PasswordEncoder passwordEncoder() {

        // BCrypt 방식의 비밀번호 암호화 객체를 생성해서 반환
        // → 비밀번호를 원래 값으로 되돌릴 수 없는 "단방향 암호화"
        // → 같은 비밀번호라도 매번 다른 결과가 나와서 보안에 강함
        return new BCryptPasswordEncoder(); // 현존 최강의 단방향 암호화. 시큐리티에 기본 내장되어 있음.

    }
}
