package com.yvonne.proyecto.manager;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.model.User;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.*;

@Service
public class TokenManager implements Serializable {
    @Value("Secretin secretado, este Secreto esta Encriptado")
    private static String SECRETO;

    @Value("${spring.datasource.google_id}")
    private String googleid;

    private static String GOOGLE_ID;

    private static UserManager userManager;

    @Autowired
    public void setUserManager(UserManager userManager) {
        TokenManager.userManager = userManager;
    }

    @Value("${spring.datasource.google_id}")
    public void setGoogleid(String googleid){
        TokenManager.GOOGLE_ID = googleid;
    }


    private static final Key SECRET = new SecretKeySpec(Base64.getDecoder().decode("U2VjcmV0aW4gc2VjcmV0YWRvLCBlc3RlIFNlY3JldG8gZXN0YSBFbmNyaXB0YWRv"),
            SignatureAlgorithm.HS256.getJcaName());

    public static String generateToken(User user) {

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
                        SECRET
                ).compact();

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
    }

    public static User getUserFromToken(HttpServletRequest request){

        String auth = request.getHeader("Authorization");
        User user;
        if(auth != null && !auth.contains("null")) {
            String token = auth.split(" ")[1];

            Base64.Decoder decoder = Base64.getDecoder();
            //esto va a pincho porque los tokens deberian ser siempre iguales.
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));

            JsonObject json = new Gson().fromJson(payload, JsonObject.class);

            Gson gson = new Gson();
            user = gson.fromJson(json.get("user"), User.class);
            return user;

        }  else {
            return null;
        }
    }
}
