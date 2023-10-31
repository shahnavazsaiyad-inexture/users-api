package com.inexture.users.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


	@Autowired
	private LoginRequestValidator loginRequestValidator;

	/**
	 * This method configures an InitBinder for login request validator
	 * 
	 * @param binder
	 */
	@InitBinder("loginRequest")
	public void initBinderLoginRequest(WebDataBinder binder) {
		binder.addValidators(loginRequestValidator);
	}
	
	/**
	 * This method provide API for user login
	 * 
	 * @param loginRequest
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<APIResponse> login(@Validated @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
		
		if(bindingResult.hasErrors()) {
			return applicationUtils.generateErrorResponse(bindingResult);	
		}
		
		Optional<LoginResponse> loginResponse = userService.login(loginRequest);
		
		if(loginResponse.isPresent()) {
			
			return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "Successfully logged in.", loginResponse.get()));
		}
		return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "Invalid credentials."));		
	}
}
