package com.sxf.project.security;

import com.sxf.project.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserProvider authService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String token = request.getHeader("Authorization");
        if(token!=null && token.startsWith("Bearer") && !request.getRequestURI().startsWith("/api/auth/")){
            token=token.substring(7);
            boolean validateToken = jwtTokenUtil.validateAccessToken(token);
            if(validateToken){
                String username = jwtTokenUtil.getUsernameFromAccessToken(token);
                UserDetails userDetails = authService.loadUserByUsername(username);
                User user = (User) authService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request,response);
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        try {
//            String jwtToken = jwtTokenUtil.extractJwtFromRequest(request);
//            logger.debug("Extracted JWT Token: " + jwtToken);
//
//            if (StringUtils.hasText(jwtToken) && jwtTokenUtil.validateToken(jwtToken)) {
//                logger.debug("JWT Token is valid");
//                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//                List<GrantedAuthority> authorities = jwtTokenUtil.getRolesFromToken(jwtToken);
//                logger.debug("Username: " + username + ", Authorities: " + authorities);
//
//                UserDetails userDetails = new User(username, "", authorities);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                logger.debug("JWT Token is invalid or not present");
//            }
//        } catch (Exception ex) {
//            logger.error("Could not set user authentication in security context", ex);
//        }
//
//        chain.doFilter(request, response);
//    }
}
