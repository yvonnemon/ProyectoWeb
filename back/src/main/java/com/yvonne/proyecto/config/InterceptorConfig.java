/*package com.yvonne.proyecto.config;

//import com.yvonne.proyecto.handler.TokenFilter;
import com.yvonne.proyecto.handler.TokenFilter;
import com.yvonne.proyecto.handler.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public TokenFilter getInterceptor(){
        return new TokenFilter();
    }

  /*  @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenFilter()).addPathPatterns("/**").excludePathPatterns("/login");
    }
}*/