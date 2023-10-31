package com.inexture.users.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.validator.UpdateUserRequestValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides endpoints related to User operations like list, update, delete user.
 * 
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController{
	
	@Autowired
	private UpdateUserRequestValidator updateUserRequestValidator;
	
	/**
	 * This method configures InitBinder for update user request validator
	 * 
	 * @param binder
	 */
	@InitBinder("userPojo")
	public void userPojoInitBinder(WebDataBinder binder) {
		binder.addValidators(updateUserRequestValidator);
	}
	
	/**
	 * This method provide API to fetch list of all users
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping
	public ResponseEntity<APIResponse> listUsers(HttpServletRequest request){
		
		try {
			String role =  (String) request.getAttribute("role");
			if(role.equalsIgnoreCase("Admin")) {
				List<UserPojo> users = userService.getAll();
				
				if(ApplicationUtils.isEmpty(users)) {
					return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "No records found."));
				}

				return ResponseEntity.ok(ApplicationUtils.generateResponse(false, users));
			}
			else {
				return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "You are not authorized to access this service."));
				
			}
			
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.badRequest().body(ApplicationUtils.generateResponse(true, "Something is wrong, contact support."));
		}		
	}

	/**
	 * This method provide API to fetch single user by userId
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<APIResponse> getUserById(@PathVariable("userId") Integer userId, HttpServletRequest request){
		try {
			String role =  (String) request.getAttribute("role");
			Integer sessionUserId = Integer.parseInt(request.getAttribute("userId").toString());
			
			if(role.equalsIgnoreCase("Admin") || userId.equals(sessionUserId)) {
				Optional<UserPojo> user = userService.getById(userId);
				
				if(user.isEmpty()) {
					return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "User not found."));
				}else {
					return ResponseEntity.ok(ApplicationUtils.generateResponse(false, user.get()));
				}
			}
			else {
				return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "You are not authorized to access other users data."));
			}
			
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}		
		return ResponseEntity.badRequest().body(ApplicationUtils.generateResponse(true, "Something is wrong, contact support."));
	}

	/**
	 * This method provide API to update user
	 * 
	 * @param userPojo
	 * @param bindingResult
	 * @param request
	 * @return
	 */
	@PostMapping
	public ResponseEntity<APIResponse> updateUser(@Validated @RequestBody UserPojo userPojo, BindingResult bindingResult, HttpServletRequest request){
		
		if(bindingResult.hasErrors()) {
			return applicationUtils.generateErrorResponse(bindingResult);
		}
		
		try {
			String role =  (String) request.getAttribute("role");
			Integer sessionUserId = Integer.parseInt(request.getAttribute("userId").toString());
			
			if(role.equalsIgnoreCase("Admin") || userPojo.getId().equals(sessionUserId)) {
				
				boolean isUpdated = userService.update(userPojo);
				if(isUpdated) {
					return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "User details updates successfully."));
				}
			}
			else {
				return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "You are not authorized to update other users data."));
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return ResponseEntity.badRequest().body(ApplicationUtils.generateResponse(true, "Something is wrong, contact support."));			
	}
	
	/**
	 * This method provide API to delete a user
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@DeleteMapping("/{userId}")
	public ResponseEntity<APIResponse> deleteUser(@PathVariable("userId") Integer userId, HttpServletRequest request){
		
		
		try {
			String role =  (String) request.getAttribute("role");
			
			if(role.equalsIgnoreCase("Admin") ) {
				
				boolean isDeleted = userService.deleteById(userId);
				if(isDeleted) {
					return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "User has been deleted."));
				}
			}
			else {
				return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "You are not authorized to delete user."));
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return ResponseEntity.badRequest().body(ApplicationUtils.generateResponse(true, "Something is wrong, contact support."));			
	}
	
}
