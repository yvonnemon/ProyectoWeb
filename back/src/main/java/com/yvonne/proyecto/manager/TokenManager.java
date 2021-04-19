package com.yvonne.proyecto.manager;

import com.google.gson.Gson;
import com.yvonne.proyecto.model.User;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.gson.io.GsonSerializer;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenManager implements Serializable {
    @Value("Secretin secretado, este Secreto esta Encriptado")
    private static String SECRETO;

    public static String generateToken(String name, String surname){

        //Gson gson = getGson();

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        //String encodedSecret = Base64.getEncoder().encode(SECRETO.getBytes(StandardCharsets.UTF_8));
        byte[] secret = Base64.getDecoder().decode("U2VjcmV0aW4gc2VjcmV0YWRvLCBlc3RlIFNlY3JldG8gZXN0YSBFbmNyaXB0YWRv");
        //byte[] secret = Base64.getEncoder().encode(SECRETO.getBytes(StandardCharsets.UTF_8));
        String asd = key.toString();
        User user = new User();
        user.setName(name);
        user.setLastname(surname);
        user.setPassword("123");
        String jws = Jwts.builder()
                //.serializeToJsonWith(new GsonSerializer(gson))
                .claim("user", user)
                .claim("role",user.getPassword())
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                .setExpiration(Date.from(Instant.ofEpochSecond(259200)))
                .signWith(
                        Keys.hmacShaKeyFor(secret)
                ).compact();

        return jws;
    }

    public boolean validateToken(String token){
        System.out.println("token recibido: "+token);
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(SECRETO.getBytes())
                .build() //esto es el payload
                .parseClaimsJws(token);

        //return new Gson().toJson(claims);
        return false;
        //return claims != null;
    }

}
