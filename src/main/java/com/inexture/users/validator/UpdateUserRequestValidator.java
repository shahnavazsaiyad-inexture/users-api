package com.inexture.users.validator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;

/**
 * This class provides custom Spring Validator for Update User request
 */
@Component
public class UpdateUserRequestValidator implements Validator {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean supports(Class<?> clazz) {

		return clazz.equals(UserPojo.class);
	}

	/**
	 * This method validates update user request and add appropreate error code 
	 */
	@Override
	public void validate(Object target, Errors errors) {

		if(!Objects.isNull(target)) {
			UserPojo user = (UserPojo) target;
			
			validateUsername(errors, user.getUsername(), user.getId());
			
			validateEmail(errors, user.getEmail(), user.getId());
		
			if(ApplicationUtils.isEmpty(user.getRole())) {
				errors.rejectValue("role", "error.field.empty");
			}
			else {
				if(!List.of("Admin","User").contains(user.getRole())) {
					errors.rejectValue("role", "error.role.invalid");					
				}
			}
		
			if(ApplicationUtils.isEmpty(user.getFirstName())) {
				errors.rejectValue("firstName", "error.field.empty");
			}
			else if(!Pattern.matches("[a-zA-Z]{1,}", user.getFirstName())){
				errors.rejectValue("firstName", "error.field.characters.only");
			}
			
			if(ApplicationUtils.isEmpty(user.getLastName())) {
				errors.rejectValue("lastName", "error.field.empty");
			}
			else if(!Pattern.matches("[a-zA-Z]{1,}", user.getLastName())){
				errors.rejectValue("lastName", "error.field.characters.only");
			}

			
			if(ApplicationUtils.isEmpty(user.getAddresses())) {
				errors.rejectValue("addresses","error.address.required");
			}else {
				for(int i=0; i< user.getAddresses().size(); i++) {
					AddressPojo address = user.getAddresses().get(i);
					
					if(ApplicationUtils.isEmpty(address.getStreet())) {
						errors.rejectValue("addresses["+i+"].street", "error.street.empty");
					}
					
					if(ApplicationUtils.isEmpty(address.getCity())) {
						errors.rejectValue("addresses["+i+"].city", "error.city.empty");
					}
					
					if(ApplicationUtils.isEmpty(address.getState())) {
						errors.rejectValue("addresses["+i+"].state", "error.state.empty");
					}
					
					if(ApplicationUtils.isEmpty(address.getCountry())) {
						errors.rejectValue("addresses["+i+"].country", "error.country.empty");
					}
					
				}
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
	 * @param userId
	 */
	private void validateEmail(Errors errors, String email,  Integer userId) {
		if(ApplicationUtils.isEmpty(email)) {
			errors.rejectValue("email", "error.field.empty");
		}
		else if(!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
			errors.rejectValue("email", "error.invalid.email");
		}
		else {
			
			long count = userService.countByEmailAndIdNot(email, userId);
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
	 * @param userId
	 */
	private void validateUsername(Errors errors, String username, Integer userId) {
		if(ApplicationUtils.isEmpty(username)) {
			errors.rejectValue("username", "error.field.empty");
		}
		else {
			
			long count = userService.countByUsernameAndIdNot(username, userId);
			if(count > 0) {
				errors.rejectValue("username", "error.field.exists");
				
			}
		}
	}

}
