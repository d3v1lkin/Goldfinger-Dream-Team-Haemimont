package com.example.config;

import com.example.models.Role;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final int EXPIRATION_DURATION = 1200000;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            user.getAuthorities()));

        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        String token = Jwts.builder()
                .setSubject(((User) authResult.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DURATION))
                .signWith(SignatureAlgorithm.HS256, JwtSecurityConstants.SECRET.getBytes())
                .compact();

        System.out.println("Generated token - " + token);
        response.setContentType("application/json");
        response.addHeader("Authorization", "Bearer " + token);

        String userRole = ((List<Role>) authResult.getAuthorities()).get(0).getAuthority();
        System.out.println(userRole);

        try {
            String jsonString = "{\"Authorization\": \"Bearer " + token + "\"" + ", \"Role\": \"" + userRole + "\"}\n";
            response.getWriter().write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
