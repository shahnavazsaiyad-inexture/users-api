package com.inexture.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.validator.RegistrationRequestValidator;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RegistrationController extends BaseController {

	@Autowired
	private RegistrationRequestValidator registrationRequestValidator;

	@InitBinder("userPojo")
	public void initBinderUserPojo(WebDataBinder binder) {
		binder.addValidators(registrationRequestValidator);
	}
	
	@PostMapping("/register")
	public ResponseEntity<APIResponse> register(@Validated @RequestBody UserPojo userPojo, BindingResult bindingResult){
		
		if(bindingResult.hasErrors()) {
			return applicationUtils.generateErrorResponse(bindingResult);	
		}
		
		try {
			
			boolean isCreated = userService.create(userPojo);
			
			if(isCreated) {
				return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "Congratulations! You are successfully registered!"));
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);			
		}
		return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "Something is wrong, contact support."));
	}
}
