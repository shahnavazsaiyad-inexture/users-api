package com.inexture.users.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.utils.ApplicationUtils;

@Component
public class LoginRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(LoginRequest.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		if(!Objects.isNull(target)) {
			LoginRequest loginRequest = (LoginRequest) target;

			if(ApplicationUtils.isEmpty(loginRequest.getUsername())) {
				errors.rejectValue("username", "error.field.empty");
			}
			
			if(ApplicationUtils.isEmpty(loginRequest.getPassword())) {
				errors.rejectValue("password", "error.field.empty");
			}
			
		}else {
			errors.reject("error.request.object.null");
		}
	}

}
