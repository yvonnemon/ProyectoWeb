package com.yvonne.proyecto.handler;

import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.manager.UserManager;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserManager userManager;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if(!Strings.isEmpty(header) && header.startsWith("Bearer ")) {
            final String token = header.replace("Bearer", "");

            com.yvonne.proyecto.model.User user = TokenManager.getUserFromToken(token);
            if (user == null){
                httpServletResponse.sendError(403);
            }

            List<GrantedAuthority> auth = new ArrayList<>();
            auth.add(new SimpleGrantedAuthority(user.getAuthorities().toString()));

            if(TokenManager.validateToken(token)) {
                UserDetails userDetails = User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(auth)
                        .build();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, auth));
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
