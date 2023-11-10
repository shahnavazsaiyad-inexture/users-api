package com.inexture.users.controller;

import java.security.Security;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.inexture.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.validator.LoginRequestValidator;

/**
 * This class provide endpoint related to login API
 * 
 */
@RestController
public class LoginController extends BaseController {

//	@Autowired
//	private LoginRequestValidator loginRequestValidator;
//
//	/**
//	 * This method configures an InitBinder for login request validator
//	 *
//	 * @param binder
//	 */
//	@InitBinder("loginRequest")
//	public void initBinderLoginRequest(WebDataBinder binder) {
//		binder.addValidators(loginRequestValidator);
//	}
//
//	/**
//	 * This method provide API for user login
//	 *
//	 * @param loginRequest
//	 * @param bindingResult
//	 * @return
//	 */
//	@PostMapping("/login")
//	public ResponseEntity<APIResponse> login(@Validated @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
//
//		if(bindingResult.hasErrors()) {
//			return applicationUtils.generateErrorResponse(bindingResult);
//		}
//
//		Optional<LoginResponse> loginResponse = userService.login(loginRequest);
//
//		if(loginResponse.isPresent()) {
//
//			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginResponse.get().getUsername(), loginRequest.getPassword());
//
//			Authentication authenticate = authenticationManager.authenticate(authenticationToken);
//
//			SecurityContextHolder.getContext().setAuthentication(authenticate);
//
//			return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "Successfully logged in.", loginResponse.get()));
//		}
//		return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "Invalid credentials."));
//	}

	/**
	 * This method defines endpoint to retrieve user details for currently logged in user
	 *
	 * @param principal
	 * @return
	 */
	@GetMapping("/login/user")
	public ResponseEntity<APIResponse> loginUser(@AuthenticationPrincipal Object principal){
		String loginUser;

		if(principal instanceof  OAuth2User){
			OAuth2User oauthUser = (OAuth2User) principal;

			loginUser = oauthUser.getAttribute("login").toString();
		}else {
			org.springframework.security.core.userdetails.User securityUser = (org.springframework.security.core.userdetails.User) principal;

			loginUser = securityUser.getUsername();
		}
		Optional<User> systemUser = userService.getByUsername(loginUser);
		if(systemUser.isPresent()){
			LoginResponse loginResponse = LoginResponse.builder().userId(systemUser.get().getId()).username(systemUser.get().getUsername()).role(systemUser.get().getRole()).build();
			return  ResponseEntity.ok(ApplicationUtils.generateResponse(false, loginResponse));
		}
		return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "Not loggedin"));
	}
}
