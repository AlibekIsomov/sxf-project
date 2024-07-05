package com.sxf.project.security;

import com.sxf.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig  {


    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    UserProvider userProvider;

    @Autowired
    UserRepository userRepository;


    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userProvider).passwordEncoder(passwordEncoder());

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/api/account/authenticate").permitAll()
                        .requestMatchers("/api/fayl/download/{id}").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:4200");
//        config.addAllowedOrigin("http://localhost:5500");
//        config.addAllowedOrigin("http://localhost:5501");
//        config.addAllowedOrigin("http://localhost:5502");
//        config.addAllowedOrigin("http://localhost:5173");
//        config.addAllowedOrigin("http://185.198.152.35");
//        config.addAllowedOrigin("https://sxf-cc.vercel.app");
//        config.addAllowedOrigin("https://api.qarshimall.uz");
//        config.addAllowedOrigin("https://dashboard.qarshimall.uz");
//        config.addAllowedOrigin("http://api.qarshimall.uz");
//        config.setAllowedMethods(List.of("GET","POST","PUT", "DELETE", "PATCH"));
//        config.setAllowedHeaders(List.of("Authorization","Content-Type","recaptcha","Content-Disposition","Access-Control-Allow-Headers","Access-Control-Expose-Headers"));
//        config.setExposedHeaders(Collections.singletonList("Content-Disposition"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","http://127.0.0.1:5173","https://api.qarshimall.uz","https://dashboard.qarshimall.uz","https://sxf-cc.vercel.app","http://185.198.152.35"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","recaptcha","Content-Disposition","Access-Control-Allow-Headers","Access-Control-Expose-Headers"));
        configuration.setExposedHeaders(Collections.singletonList("Content-Disposition"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

