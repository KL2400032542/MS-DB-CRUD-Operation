package com.klu.springmvc.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	public String generateJWT(Map<String, String> u1, String role, Integer id) {
		String key = "dfygfygvidkjvhruiyfervuoefviheruireghigh";
		SecretKey skey = Keys.hmacShaKeyFor(key.getBytes());
		
		Map<String, String> claim = new HashMap<>();
		claim.put("un", u1.get("username"));
		claim.put("role", role);
		claim.put("id", id.toString());
		
		return Jwts.builder()
		.claims(claim)
		.issuedAt(new Date())
		.setExpiration(new Date(new Date().getTime() + 86400000))
		.signWith(skey)
		.compact();
	}
	
	public Map<String, String> validateJWT (String token) throws Exception {
		String key = "dfygfygvidkjvhruiyfervuoefviheruireghigh";
		SecretKey skey = Keys.hmacShaKeyFor(key.getBytes());
		
		Claims claim = Jwts.parser()
		.verifyWith(skey)
		.build()
		.parseSignedClaims(token)
		.getPayload();
		
		if(claim == null || claim.getExpiration().before(new Date())) {
			throw new Exception("Token Invalid!");
		}
		
		Map<String, String> parsedJWT = new HashMap<>();
		parsedJWT.put("username", claim.get("un").toString());
		parsedJWT.put("role", claim.get("role").toString());
		parsedJWT.put("id", claim.get("id").toString());
		
		return parsedJWT;	
	}
}
