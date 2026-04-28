package com.example.demo.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;


@Service
public class JwtService{

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-ms}") long expirationMs
    ){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username){
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token){
        return parse(token).getSubject();
    }

    public boolean isValid(String token){
        try{
            Claims claims = parse(token);
            return claims.getExpiration().after(new Date());
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private Claims parse(String token){
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public long getExpirationMs(){
        return expirationMs;
    }
}