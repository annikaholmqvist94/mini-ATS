package com.miniats.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Validates JWT tokens issued by Supabase Auth using JWKS endpoint.
 */
@Component
public class JwtTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final String jwksUrl;
    private final RestTemplate restTemplate;
    private Map<String, Object> jwks;

    public JwtTokenValidator(
            @Value("${supabase.jwt.jwks-url}") String jwksUrl,
            RestTemplate restTemplate
    ) {
        this.jwksUrl = jwksUrl;
        this.restTemplate = restTemplate;
        this.jwks = fetchJwks();
        logger.info("✅ JWT Token Validator initialized with JWKS URL: {}", jwksUrl);
    }

    /**
     * Fetch JWKS from Supabase
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchJwks() {
        try {
            Map<String, Object> response = restTemplate.getForObject(jwksUrl, Map.class);
            logger.info("✅ JWKS fetched successfully");
            return response;
        } catch (Exception e) {
            logger.error("❌ Failed to fetch JWKS: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch JWKS", e);
        }
    }

    /**
     * Validate JWT token and extract claims
     */
    public Claims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public Key resolveSigningKey(JwsHeader header, Claims claims) {
                            String kid = header.getKeyId();

                            // Find matching key in JWKS
                            var keys = (java.util.List<Map<String, Object>>) jwks.get("keys");
                            for (Map<String, Object> key : keys) {
                                if (kid.equals(key.get("kid"))) {
                                    return buildPublicKey(key);
                                }
                            }

                            throw new RuntimeException("No matching key found for kid: " + kid);
                        }
                    })
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                logger.warn("❌ Token expired for: {}", claims.getSubject());
                return null;
            }

            logger.debug("✅ Token validated for user: {}", claims.get("email", String.class));
            return claims;

        } catch (Exception e) {
            logger.error("❌ Token validation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Build EC public key from JWK
     */
    @SuppressWarnings("unchecked")
    private PublicKey buildPublicKey(Map<String, Object> jwk) {
        try {
            String kty = (String) jwk.get("kty");

            if (!"EC".equals(kty)) {
                throw new RuntimeException("Unsupported key type: " + kty);
            }

            // Get curve parameters
            String crv = (String) jwk.get("crv");
            String x = (String) jwk.get("x");
            String y = (String) jwk.get("y");

            // Decode base64url coordinates
            byte[] xBytes = Base64.getUrlDecoder().decode(x);
            byte[] yBytes = Base64.getUrlDecoder().decode(y);

            BigInteger xCoord = new BigInteger(1, xBytes);
            BigInteger yCoord = new BigInteger(1, yBytes);

            // Create EC point
            ECPoint point = new ECPoint(xCoord, yCoord);

            // Get EC parameter spec for P-256
            java.security.spec.ECGenParameterSpec ecSpec =
                    new java.security.spec.ECGenParameterSpec("secp256r1");

            java.security.AlgorithmParameters params =
                    java.security.AlgorithmParameters.getInstance("EC");
            params.init(ecSpec);
            ECParameterSpec ecParams = params.getParameterSpec(ECParameterSpec.class);

            // Create public key spec
            ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, ecParams);

            // Generate public key
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(pubKeySpec);

        } catch (Exception e) {
            logger.error("❌ Failed to build public key: {}", e.getMessage());
            throw new RuntimeException("Failed to build public key", e);
        }
    }

    /**
     * Extract user email from token
     */
    public String getEmailFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("email", String.class);
        }
        return null;
    }

    /**
     * Extract user ID from token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token) {
        return validateToken(token) != null;
    }
}