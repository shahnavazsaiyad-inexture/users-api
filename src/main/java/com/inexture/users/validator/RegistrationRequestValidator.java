package com.inexture.users.validator;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.inexture.users.pojo.UserPojo;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
/**
 * This class provides custom Spring Validator for Registration request
 */
@Component
public class RegistrationRequestValidator implements Validator {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean supports(Class<?> clazz) {

		return clazz.equals(UserPojo.class);
	}

	/**
	 * This method validates registration request and add appropreate error code 
	 */
	@Override
	public void validate(Object target, Errors errors) {

		if(!Objects.isNull(target)) {
			UserPojo user = (UserPojo) target;
			
			validateUsername(errors, user.getUsername());
			
			validateEmail(errors, user.getEmail());
		
			if(ApplicationUtils.isEmpty(user.getRole())) {
				errors.rejectValue("role", "error.field.empty");
			}
			
			if(ApplicationUtils.isEmpty(user.getPassword())) {
				errors.rejectValue("password", "error.field.empty");
			}
			
			if(ApplicationUtils.isEmpty(user.getAddresses())) {
				errors.rejectValue("addresses","error.address.required");
			}
			
		}else {
			errors.reject("error.request.object.null");
		}
	}

	/**
	 * This method is used to validate email field
	 * 
	 * @param errors
	 * @param email
	 */
	private void validateEmail(Errors errors, String email) {
		if(ApplicationUtils.isEmpty(email)) {
			errors.rejectValue("email", "error.field.empty");
		}
		else if(!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
			errors.rejectValue("email", "error.invalid.email");
		}
		else {
			
			long count = userService.countByEmail(email);
			if(count > 0) {
				errors.rejectValue("email", "error.field.exists");				
			}
		}
		
	}

	/**
	 * This method is used to validate username field
	 * 
	 * @param errors
	 * @param username
	 */
	private void validateUsername(Errors errors, String username) {
		if(ApplicationUtils.isEmpty(username)) {
			errors.rejectValue("username", "error.field.empty");
		}
		else {
			
			long count = userService.countByUsername(username);
			if(count > 0) {
				errors.rejectValue("username", "error.field.exists");
				
			}
		}
	}

}
