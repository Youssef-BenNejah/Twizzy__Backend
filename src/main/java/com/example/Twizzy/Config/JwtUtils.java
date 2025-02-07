package com.example.Twizzy.Config;

import com.example.Twizzy.Entities.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(role -> "ROLE_" + role.name()).collect(Collectors.toSet()))
                .claim("userId", user.getId())
                .claim("Username", user.getUsername())
                .claim("Email", user.getEmail())


                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }





    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            System.out.println("✅ Token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token Expired!");
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed JWT Token!");
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            System.out.println("❌ Invalid JWT Token!");
        }
        return false;
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // Supprime "Bearer "
        }
        return null;
    }
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles != null
                ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    public Set<String> getRolesFromToken(String token) {
        return ((Set<String>) Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("roles"))
                .stream()
                .map(role -> role.replace("ROLE_", ""))  // Remove the ROLE_ prefix here
                .collect(Collectors.toSet());
    }



}
