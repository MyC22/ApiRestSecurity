package com.example.RestApi.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Mahesh
 */
@Component
public class JWTUtil {
    @Value("${security.jwt.key.private}")
    private String privatekey;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.ttlMillis}")
    private long ttlMillis;

    private final Logger log = LoggerFactory
            .getLogger(JWTUtil.class);

//    /**
//     * Create a new token.
//     *
//     * @param id
//     * @param subject
//     * @return
//     */
   public String createToken(Authentication authentication) {

       Algorithm algorithm = Algorithm.HMAC256(this.privatekey);

       String username = authentication.getPrincipal().toString();

       String authorities = authentication.getAuthorities()
               .stream()
               .map(grantedAuthority -> grantedAuthority.getAuthority())
               .collect(Collectors.joining(","));

       String jwtTtoken = JWT.create()
               .withIssuer(this.issuer)
               .withSubject(username)
               .withClaim("authorities", authorities)
               .withIssuedAt(new Date())
               .withExpiresAt(new Date(System.currentTimeMillis() + ttlMillis))
               .withJWTId(UUID.randomUUID().toString())
               .withNotBefore(new Date(System.currentTimeMillis()))
               .sign(algorithm);
       return jwtTtoken;


//       long nowMillis = System.currentTimeMillis();
//        Date now = new Date(nowMillis);
//
//        //  sign JWT with our ApiKey secret
//        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(privatekey);
//        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
//
//        //  set the JWT Claims
//        JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
//                .signWith(signatureAlgorithm, signingKey);
//
//        if (ttlMillis >= 0) {
//            long expMillis = nowMillis + ttlMillis;
//            Date exp = new Date(expMillis);
//            builder.setExpiration(exp);
//        }
//
//        // Builds the JWT and serializes it to a compact, URL-safe string
//        return builder.compact();
    }

    public DecodedJWT valiteToken(String token){
       try {
         Algorithm algorithm = Algorithm.HMAC256(this.privatekey);
           JWTVerifier verifier = JWT.require(algorithm)
                   .withIssuer(this.issuer)
                   .build();
           DecodedJWT decodedJWT = verifier.verify(token);
           return decodedJWT;
       } catch (JWTVerificationException exception){
           throw new JWTVerificationException("Token invalid, not Authorized");
       }
    }

    public String extractUsername(DecodedJWT decodedJWT){
       return decodedJWT.getSubject().toString();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
       return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
       return decodedJWT.getClaims();
    }

//
//
//    /**
//     * Method to validate and read the JWT
//     *
//     * @param jwt
//     * @return
//     */
//    public String getValue(String jwt) {
//        // This line will throw an exception if it is not a signed JWS (as
//        // expected)
//        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(privatekey))
//                .parseClaimsJws(jwt).getBody();
//
//        return claims.getSubject();
//    }
//
//    /**
//     * Method to validate and read the JWT
//     *
//     * @param jwt
//     * @return
//     */
//   public String getKey(String jwt) {
//        // This line will throw an exception if it is not a signed JWS (as
//        // expected)
//        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(privatekey))
//                .parseClaimsJws(jwt).getBody();
//
//        return claims.getId();
//    }
}
