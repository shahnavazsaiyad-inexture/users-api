package com.inexture.users.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.utils.JwtUtility;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizationFilter implements Filter {

	@Autowired
	private JwtUtility jwtUtility;
	
	@Autowired
	private ApplicationUtils applicationUtils;
	
	List<String> publicUrls = List.of("/register","/login", "/resetpassword");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		httpResponse.addHeader("Access-Control-Allow-Origin", "*");
		httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setHeader("Access-Control-Allow-Headers",
		            "Authorization, content-type, x-gwt-module-base, x-gwt-permutation, clientid, longpush");
		
		String requestUri = httpRequest.getRequestURI();

		if(publicUrls.stream().anyMatch(path -> requestUri.startsWith(path))){	
			chain.doFilter(request, response);			
		}else {
			
			String authHeader = httpRequest.getHeader("Authorization");

			if(!ApplicationUtils.isEmpty(authHeader) && authHeader.contains("Bearer")) {

				String jwtToken = authHeader.replace("Bearer ", "");
				DecodedJWT decodedJWT = jwtUtility.decodeToken(jwtToken);
				
				if(Objects.isNull(decodedJWT)) {
					rejectAndSendJsonResponse(response, "Session expired please login again!");
				}
				else {
					Map<String, Claim> claims = decodedJWT.getClaims();
					if(!Objects.isNull(claims)) {
						claims.forEach((key, claim) -> {
							request.setAttribute(key, claim.asString());
						});
					}

					chain.doFilter(request, response);
				}
			}
			else {
				rejectAndSendJsonResponse(response, "Authorization token not found!");
			}
		}

	}

	private void rejectAndSendJsonResponse(ServletResponse response, String message) throws IOException {
		
		APIResponse apiResponse = ApplicationUtils.generateResponse(true, message, null);
		String jsonResponse = applicationUtils.generateJsonFromObject(apiResponse);
		
		if(!ApplicationUtils.isEmpty(jsonResponse)) {
			response.getWriter().print(jsonResponse);
		}
		
	}

}
