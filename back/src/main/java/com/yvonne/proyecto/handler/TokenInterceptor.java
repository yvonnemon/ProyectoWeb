package com.yvonne.proyecto.handler;

import com.yvonne.proyecto.manager.TokenManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("estas en el handler");
        String auth = request.getHeader("Authorization");
       String auth2 = request.getHeader("Gauth");
        if(auth == null && auth2 == null){
            return true;
        }
        System.out.println(auth);

        if(!auth2.contains("null")){
            String gToken = auth2.split(" ")[1];

            if(!TokenManager.validateGoogleToken(gToken)){
                response.sendError(403);
                return false;
            } else {
                return true;
            }
        } else {
            String token = auth.split(" ")[1];

            if(!TokenManager.validateToken(token)){
                response.sendError(403);
                return false;
            } else {
                return true;
            }

        }

    }
}
