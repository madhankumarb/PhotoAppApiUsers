package com.appsdeveloper.photoapp.api.users.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    
	Environment environment;

    public AuthorizationFilter(AuthenticationManager authManager, Environment environment) {
        super(authManager);
        this.environment = environment;
    }
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = req.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(authorizationHeader);
       
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }  
    
    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {

         String token = authorizationHeader.replace("Bearer", "");

         String userId = Jwts.parser()
                 .setSigningKey(environment.getProperty("token.secret"))
                 .parseClaimsJws(token)
                 .getBody()
                 .getSubject();

         if (userId == null) {
             return null;
         }
   
         return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

     }
}
