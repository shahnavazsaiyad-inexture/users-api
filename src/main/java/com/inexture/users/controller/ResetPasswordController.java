package com.inexture.users.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.ResetPasswordRequest;
import com.inexture.users.utils.ApplicationUtils;

@RestController
@RequestMapping("/resetpassword")
public class ResetPasswordController extends BaseController{

	@GetMapping("/{username}")
	public ResponseEntity<APIResponse> sendResetPasswordToken(@PathVariable("username") String username){
		
		Optional<User> user = userService.getByUsername(username);
		
		if(user.isPresent()) {
			
			resetPasswordTokenService.generateAndSendToken(user.get());
			
			return ResponseEntity.ok(ApplicationUtils.generateResponse(false, "A link has been sent to your Email for reset your password!"));
		}else {
			return ResponseEntity.ok(ApplicationUtils.generateResponse(true, "User does not exists!"));
		}		
	}
	
	@PostMapping
	public ResponseEntity<APIResponse> resetPassword( @RequestBody ResetPasswordRequest resetPasswordRequest){
		APIResponse response = new APIResponse();
		resetPasswordTokenService.resetPassword(resetPasswordRequest, response);
		return ResponseEntity.ok(response);
	}
}
