package com.josp.system.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT (JSON Web Token) utility class for the JOSP System.
 * Provides methods for token generation, validation, and claim extraction.
 * 
 * <p>This class handles:
 * <ul>
 *   <li>Token generation with claims</li>
 *   <li>Token expiration validation</li>
 *   <li>Username extraction from token</li>
 *   <li>Custom claims retrieval</li>
 * </ul>
 *
 * <p>The token is signed using HMAC-SHA algorithm with a secret key.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * Gets the signing key for JWT signature verification.
     *
     * @return the SecretKey for HMAC-SHA signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details containing username
     * @return the generated JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        return generateToken(claims);
    }

    /**
     * Generates a JWT token with the specified claims.
     *
     * @param claims the claims to include in the token
     * @return the generated JWT token string
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(generateExpirationDate())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates the expiration date based on configured expiration time.
     *
     * @return the expiration Date
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token string
     * @return the username (subject claim)
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided resolver function.
     *
     * @param <T> the type of the claim to extract
     * @param token the JWT token string
     * @param claimsResolver function to extract the desired claim
     * @return the extracted claim value
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and retrieves all claims from the JWT token.
     *
     * @param token the JWT token string
     * @return the Claims object containing all token claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates a JWT token against user details.
     * Token is valid if:
     * <ul>
     *   <li>Username in token matches the provided userDetails username</li>
     *   <li>Token has not expired</li>
     * </ul>
     *
     * @param token the JWT token string
     * @param userDetails the user details to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token string
     * @return true if token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token string
     * @return the expiration Date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}
