package com.digi01.CMonroyProgramacionNCapasSpring.Security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

//    @Value("${jwt.expiration}")
//    private long expirationMs;

    public String generateToken(String username, List<String> roles,int idUsuario) throws JOSEException {

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("roles", roles)
                .claim("idUsuario", idUsuario)
                //.expirationTime(new Date(System.currentTimeMillis() + expirationMs))
                .issueTime(new Date())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claims);

        signedJWT.sign(new MACSigner(secretKey.getBytes()));

        return signedJWT.serialize();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
    try {
        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(new MACVerifier(secretKey.getBytes()));

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!verified || (expiration != null && expiration.before(new Date()))) {
            return false;
        }

        String username = signedJWT.getJWTClaimsSet().getSubject();
        return username.equals(userDetails.getUsername());

    } catch (Exception e) {
        return false;
    }
}


    public String getUsernameFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public List<String> getRolesFromToken(String token) throws ParseException {
        return (List<String>) SignedJWT.parse(token)
                .getJWTClaimsSet()
                .getClaim("roles");
    }
}
