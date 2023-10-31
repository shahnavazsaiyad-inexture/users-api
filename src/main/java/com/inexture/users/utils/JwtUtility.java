package com.inexture.users.utils;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.extern.slf4j.Slf4j;

/**
 * This class provide utility methods related to JWT
 * 
 */
@Component
@Slf4j
public class JwtUtility {

	@Value("${jwt.secret}")
	private String secret;
	
	/**
	 * This method is used to generate JWT token
	 * 
	 * @param username
	 * @param role
	 * @param userId
	 * @return
	 */
	public String createToken(String username, String role, String userId) {
		try {
			
			Algorithm algorithm = Algorithm.HMAC256(secret);

			String token = JWT.create()
			        .withIssuer("auth0")
			        .withClaim("username", username)
			        .withClaim("role", role)
			        .withClaim("userId", userId)
			        .withExpiresAt(Instant.now().plusSeconds(3600))
			        .sign(algorithm);

			return token;
			
		} catch (Exception exception){
			log.error(exception.getMessage(), exception);
		}
		return null;
	}
	
	/**
	 * This method is used to decode JWT token
	 * 
	 * @param token
	 * @return
	 */
	public DecodedJWT decodeToken(String token) {
		DecodedJWT decodedJWT = null;
		try {
		    Algorithm algorithm = Algorithm.HMAC256(secret);
		    JWTVerifier verifier = JWT.require(algorithm)
				    		.withIssuer("auth0")
				    		.build();
		        
		    decodedJWT = verifier.verify(token);
		    
		} catch(SignatureVerificationException | TokenExpiredException exception) {
			log.error(exception.getMessage());
		}
		catch (Exception exception){
			log.error(exception.getMessage(), exception);
		}
		return decodedJWT;
	}
	
}
