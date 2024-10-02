package com.demo.java_jwt.util;

import com.demo.java_jwt.exception.ResourceNotFoundException;
import com.demo.java_jwt.model.User;
import com.demo.java_jwt.model.response.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtils implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    public static final int JWT_TOKEN_VALIDITY = 1000 * 60 * 30;

    public static final int JWT_REFRESH_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthResponse authenticate(Object object, String username, String password, boolean refreshTokenFlag) throws ResourceNotFoundException {
        String token;
        String refreshToken;
        try {
            if (!refreshTokenFlag) {
                final Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(username, password));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            token = generateToken(username,object);
            refreshToken = refreshToken(token);
        } catch (BadCredentialsException e) {
            if (e.getMessage().contains("UsernameNotFound")) {
                throw new ResourceNotFoundException("INVALID_USERNAME");
            } else if (e.getMessage().contains("Bad credentials")) {
                throw new ResourceNotFoundException("INVALID_PASSWORD");
            } else {
                throw new ResourceNotFoundException("INVALID_CREDENTIAL");
            }
        }
        return new AuthResponse(username, token, refreshToken);
    }


    // retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(String username, Object object) {
        Map<String,Object> claims = new HashMap<>();
        Integer userId;

        User user = (User) object;
        claims.put("userId",user.getId());
        userId = user.getId();
        return getAccessToken(claims, username,userId);
    }

    public String getAccessToken(Map<String, Object> claims, String userName,Integer userId) {
        return Jwts.builder()
                .setId(userId.toString())
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String refreshToken(String token) {
        final Claims claims = extractAllClaims(token);
        claims.setIssuedAt(new Date(System.currentTimeMillis()));
        claims.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final UserDetails userDetails) {
        final List<SimpleGrantedAuthority> authorities =new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

}
