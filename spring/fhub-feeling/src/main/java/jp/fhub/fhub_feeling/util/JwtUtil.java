package jp.fhub.fhub_feeling.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String secretKey;

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60L * 60L * 1L;
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60L * 60L * 24L * 14L;

    public static final String ROLES_CLAIM = "roles";

    // JWTを生成
    public String generateToken(String email, List<String> roles) {
        Date issuedAt = new Date();
        Date notBefore = new Date(issuedAt.getTime());
        Date expiresAt = new Date(issuedAt.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        return JWT.create()
                .withSubject(email)
                .withClaim(ROLES_CLAIM, roles)
                .withIssuedAt(Instant.now())
                .withNotBefore(notBefore)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secretKey));
    }
    
    // リフレッシュトークンを生成
    public String generateRefreshToken(String email, List<String> roles) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        return JWT.create()
                .withSubject(email)
                .withClaim(ROLES_CLAIM, roles)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secretKey));
    }

    // トークンを検証してデコード
    public DecodedJWT validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token);
    }

    // クレームを抽出
    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        return validateToken(token).getClaim(ROLES_CLAIM).asList(String.class);
    }

    // トークンの有効期限を確認
    public boolean isTokenExpired(String token) {
        return validateToken(token).getExpiresAt().before(new Date());
    }

    public String validateTokenAndRetrieveSubject(String token) {
        return validateToken(token).getSubject();
    }

}