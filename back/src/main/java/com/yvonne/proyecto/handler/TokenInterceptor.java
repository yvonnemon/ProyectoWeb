package com.yvonne.proyecto.handler;

import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.model.Role;
import com.yvonne.proyecto.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return true;
        }
        String token = auth.split(" ")[1];

        if (!TokenManager.validateToken(token)) {
            response.sendError(403);
            return false;
        } else {
            User user = TokenManager.getUserFromToken(token);
            if (user.getRole().equals(Role.EMPLOYEE)) {
                String s = request.getRequestURI();
                if(!authorizedRoles(s)) {
                    response.sendError(403);
                }
                return authorizedRoles(s);
            } else {
                return true;
            }
        }
    }

    private boolean authorizedRoles(String url) {
        // TODO spring security C:\Users\Yvonne\IdeaProjects\base
        List<String> employee = Arrays.asList("/document/download", "/document/userdocs", "/user/user", "/user/update", "/document/employee", "/calendar/next", "/calendar/insert", "/calendar/users", "/calendar/delete", "/calendar/update");
        return employee.contains(url);
    }
}
