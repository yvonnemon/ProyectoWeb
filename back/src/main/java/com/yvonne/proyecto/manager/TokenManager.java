package com.yvonne.proyecto.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.yvonne.proyecto.model.User;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class TokenManager implements Serializable {
    @Value("Secretin secretado, este Secreto esta Encriptado")
    private static String SECRETO;

    private static final Key SECRET = new SecretKeySpec(Base64.getDecoder().decode("U2VjcmV0aW4gc2VjcmV0YWRvLCBlc3RlIFNlY3JldG8gZXN0YSBFbmNyaXB0YWRv"),
            SignatureAlgorithm.HS256.getJcaName());

    public static String generateToken(User user) {

        user.setPassword("");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);
        String jws = Jwts.builder()
                .claim("user", user)
                .claim("role", user.getRole())
                .setIssuedAt(today)
                .setExpiration(c.getTime())
                .signWith(
                        SECRET
                ).compact();

        return jws;
    }

    public static boolean validateToken(String token) {
        System.out.println("token recibido: " + token);

        try {
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(SECRET)
                    .build() //esto es el payload
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("El token no es valido");
            System.out.println(e);
            return false;
        }
        //Date fecha = jws.getBody().getExpiration();
    }

    public static User getTokenUser(String token) {
        Base64.Decoder decoder = Base64.getDecoder();
        ///esto va a pincho porque los tokens deberian ser siempre iguales.
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));

        JsonObject json = new Gson().fromJson(payload, JsonObject.class);

        Gson gson = new Gson();
        User user = gson.fromJson(json.get("user"), User.class);
        return user;
    }

}
