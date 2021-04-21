package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.User;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenManager implements Serializable {
    @Value("Secretin secretado, este Secreto esta Encriptado")
    private static String SECRETO;

    public static String generateToken(User user){

        //Gson gson = getGson();
    //TODO cambiar lo qeu se guarda en el token, los datos
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        //String encodedSecret = Base64.getEncoder().encode(SECRETO.getBytes(StandardCharsets.UTF_8));
        byte[] secret = Base64.getDecoder().decode("U2VjcmV0aW4gc2VjcmV0YWRvLCBlc3RlIFNlY3JldG8gZXN0YSBFbmNyaXB0YWRv");
        //byte[] secret = Base64.getEncoder().encode(SECRETO.getBytes(StandardCharsets.UTF_8));
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);
        String jws = Jwts.builder()
                .claim("user", user)
                .claim("role",user.getPassword())
                .setIssuedAt(today)
                .setExpiration(c.getTime())
                .signWith(
                        Keys.hmacShaKeyFor(secret)
                ).compact();

        return jws;
    }

    public static boolean validateToken(String token){
        System.out.println("token recibido: "+token);

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode("U2VjcmV0aW4gc2VjcmV0YWRvLCBlc3RlIFNlY3JldG8gZXN0YSBFbmNyaXB0YWRv"),
                SignatureAlgorithm.HS256.getJcaName());
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(hmacKey)
                .build() //esto es el payload
                .parseClaimsJws(token);

        return true;
    }

}
