package com.inshining.poke.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inshining.poke.domain.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@PropertySource("classpath:jwt.yml")
@Service
public class TokenProvider {
    private final String secretKey;
    private final long expirationMinutes;
    private final long refreshExpirationHours;
    private final String issuer;
    private final long reissueLimit;
    private final String algorithmWay;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenProvider(
            @Value("${secret-key}") String secretKey,
            @Value("${expiration-minutes}") long expirationMinutes,
            @Value("${refresh-expiration-hours}") long refreshExpirationHours,
            @Value("${issuer}") String issuer,
            @Value("${algorithmWay}") String algorithmWay,
            UserRefreshTokenRepository userRefreshTokenRepository
    ){
        this.secretKey = secretKey;
        this.expirationMinutes = expirationMinutes;
        this.refreshExpirationHours = refreshExpirationHours;
        this.issuer = issuer;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.algorithmWay = algorithmWay;
        reissueLimit = refreshExpirationHours * 60 / expirationMinutes;
    }
    public String createAccessToken(String userSpec){
        Algorithm algorithm = Algorithm.HMAC256(algorithmWay);

        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime currentTime = LocalDateTime.now(koreaZone);
        LocalDateTime expiredAccessTime = currentTime.plus(2, ChronoUnit.HOURS);
        LocalDateTime expiredRefreshTime = currentTime.plus(7, ChronoUnit.DAYS);


        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userSpec)
                .withIssuedAt(Date.from(currentTime.atZone(koreaZone).toInstant()))
                .withExpiresAt(Date.from(expiredAccessTime.atZone(koreaZone).toInstant()))
                .sign(algorithm);
    }

    public String validateTokenAndGetSubject(String token){
        Algorithm algorithm = Algorithm.HMAC256("gdscys2023");
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        return username;
    }


}
