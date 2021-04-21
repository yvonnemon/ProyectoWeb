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
        if(auth == null){
            return true;
        }

        System.out.println(auth);
        System.out.println(auth);
        String token = auth.split(" ")[1];


        //TokenManager tokenManager = Utils.getBean(TokenManager.class);

        return TokenManager.validateToken(token);
    }

}
