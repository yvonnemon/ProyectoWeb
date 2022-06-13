package com.yvonne.proyecto.config;

import com.yvonne.proyecto.handler.TokenFilter;
import com.yvonne.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    TokenFilter tokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(
                ((request, response, authException) -> response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        authException.getMessage()))
        )
                .and()
                .authorizeRequests()
                //Permite que todas las rutas pertenecientes a /login
                .antMatchers("/auth/login").permitAll()
                //Protege el resto de rutas
                .anyRequest().authenticated()
                .and()
                //Añade el filtro de JWT a la cadena de filtros de Spring Security después del UsernamePasswordAuthenticationFilter
                .addFilterAfter(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
