package com.green.boardauth.configuration.security;

// JWT 관련 상수값들을 담고 있는 클래스
import com.green.boardauth.configuration.constants.ConstJwt;

// JWT에 담길 사용자 정보 모델
import com.green.boardauth.configuration.model.JwtUser;

// JWT 생성 관련 라이브러리
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// 로그 출력용
import lombok.extern.slf4j.Slf4j;

// 스프링 빈으로 등록하기 위한 어노테이션
import org.springframework.stereotype.Component;

// JSON 변환용 ObjectMapper (Jackson)
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j // log.info(), log.error() 등을 사용할 수 있게 해줌
@Component // 이 클래스를 스프링 컨테이너에 빈으로 등록
public class JwtTokenProvider {

    // 객체 → JSON 문자열 변환을 담당 (직렬화)
    private final ObjectMapper objectMapper;

    // JWT 관련 설정값(issuer, secretKey, claimKey 등)을 담은 클래스
    private final ConstJwt constJwt;

    // JWT 서명(Signature)에 사용되는 비밀 키
    private final SecretKey secretKey;

    // 생성자 주입
    public JwtTokenProvider(ObjectMapper objectMapper, ConstJwt constJwt) {
        this.objectMapper = objectMapper;
        this.constJwt = constJwt;

        // Base64로 인코딩된 secretKey를 디코딩해서 HMAC 키 생성
        this.secretKey = Keys.hmacShaKeyFor( Decoders.BASE64.decode(constJwt.getSecretKey()) );

        // 설정값이 제대로 들어왔는지 확인용 로그
        log.info("constJwt: {}", this.constJwt);
    }

    // Access Token 생성 메서드
    // - 로그인 성공 시 클라이언트에게 전달되는 토큰
    // - 비교적 짧은 유효시간을 가짐
    // - 실제 API 요청 시 Authorization(인증) 헤더에 담겨 사용됨
    public String generateAccessToken(JwtUser jwtUser) {
        // 공통 토큰 생성 메서드(generateToken)를 호출하면서
        // Access Token 전용 유효시간을 전달
        return generateToken(jwtUser, constJwt.getAccessTokenValidityMilliseconds());
    }

    // Refresh Token 생성 메서드
    // - Access Token이 만료되었을 때 재발급 용도로 사용
    // - 상대적으로 긴 유효시간을 가짐
    // - 보통 서버(DB 또는 Redis)에 저장해서 관리
    public String generateRefreshToken(JwtUser jwtUser) {
        // 공통 토큰 생성 메서드(generateToken)를 호출하면서
        // Refresh Token 전용 유효시간을 전달
        return generateToken(jwtUser, constJwt.getRefreshTokenValidityMilliseconds());
    }

    /**
     JWT 토큰 생성 메서드
     @param jwtUser  토큰에 담을 사용자 정보
     @param tokenValidityMilliSeconds 토큰 유효 시간 (밀리초)
     @return 생성된 JWT 문자열
     */
    public String generateToken(JwtUser jwtUser, long tokenValidityMilliSeconds) {

        // 현재 시간 (토큰 생성 시점)
        Date now = new Date();

        return Jwts.builder()
                // JWT Header
                .header()
                .type(constJwt.getBearerFormat()) // 토큰 타입 (보통 JWT)
                .and()

                // JWT Payload
                .issuer(constJwt.getIssuer()) // 토큰 발급자
                .issuedAt(now) // 토큰 생성 시간
                .expiration( new Date(now.getTime() + tokenValidityMilliSeconds) ) // 토큰 만료 시간

                // 사용자 정보를 claim에 JSON 문자열 형태로 저장
                .claim( constJwt.getClaimKey(), makeClaimByUserToJson(jwtUser) )

                // JWT 생성 완료
                .compact(); // 최종 JWT 문자열 생성
    }

    /**
     * JwtUser 객체를 JSON 문자열로 변환
     * → JWT claim에 저장하기 위함
     */
    public String makeClaimByUserToJson(JwtUser jwtUser) {
        return objectMapper.writeValueAsString(jwtUser);  // 객체 → JSON 문자열 (직렬화)
    }
}
