package com.yvonne.proyecto.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenManager implements Serializable {
    private static String SECRETO;

    private static UserManager userManager;

    @Autowired
    public void setUserManager(UserManager userManager) {
        TokenManager.userManager = userManager;
    }

    @Value("${spring.datasource.secreto}")
    public void setSecret(String secreto) {
        TokenManager.SECRETO = secreto;
    }

    public static String generateToken(User user) {
        //m
        Key secret = new SecretKeySpec(Base64.getDecoder().decode(SECRETO),
                SignatureAlgorithm.HS256.getJcaName());
        user.setPassword("");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 1);
        return Jwts.builder()
                .claim("user", user)
                .claim("role", user.getRole())
                .setIssuedAt(today)
                .setExpiration(c.getTime())
                .signWith(
                        secret
                ).compact();

    }

    public static boolean validateToken(String token) {
        Key secret = new SecretKeySpec(Base64.getDecoder().decode(SECRETO),
                SignatureAlgorithm.HS256.getJcaName());

        System.out.println("token recibido: " + token);

        try {
            Jwts.parserBuilder().setSigningKey(secret)
                    .build() //esto es el payload
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("El token no es valido");
            System.out.println(e);
            return false;
        }
    }

    public static User getUserFromToken(String token) {
        if(validateToken(token)){
            User user;

            Base64.Decoder decoder = Base64.getDecoder();
            //esto va a pincho porque los tokens deberian ser siempre iguales.
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));

            JsonObject json = new Gson().fromJson(payload, JsonObject.class);

            Gson gson = new Gson();
            user = gson.fromJson(json.get("user"), User.class);
            return user;
        } else{
            return null;
        }

    }

    public static User getUserFromRequest(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        User user;
        final String token = header.replace("Bearer", "");
        if (header != null && !header.contains("null") && validateToken(token)) {

            Base64.Decoder decoder = Base64.getDecoder();
            //esto va a pincho porque los tokens deberian ser siempre iguales.
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));

            JsonObject json = new Gson().fromJson(payload, JsonObject.class);

            Gson gson = new Gson();
            user = gson.fromJson(json.get("user"), User.class);
            return user;

        } else {
            return null;
        }
    }
}
