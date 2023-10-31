package com.inexture.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.service.ResetPasswordTokenService;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.utils.JwtUtility;
import com.inexture.users.validator.LoginRequestValidator;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends BaseControllerTest{

	/**
	 * Test case for success scenario of login API
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginSuccess() throws Exception {
		
		LoginResponse loginResponse = LoginResponse.builder().userId(1).username("admin").role("Admin").build();
		

		when(loginRequestValidator.supports(any())).thenReturn(true);
		when(userService.login(any())).thenReturn(Optional.of(loginResponse));
		
		LoginRequest loginRequest = createLoginRequest();
		String requestBody = objectMapper.writeValueAsString(loginRequest);
		mockMvc.perform(post("/login").contentType("application/json").content(requestBody))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(false))
				.andExpect(jsonPath("$.message").value("Successfully logged in."));
	}

	/**
	 * Test case for failure scenario of login API
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginFailure() throws Exception {
		
		when(loginRequestValidator.supports(any())).thenReturn(true);
		when(userService.login(any())).thenReturn(Optional.empty());
		
		LoginRequest loginRequest = createLoginRequest();
		String requestBody = objectMapper.writeValueAsString(loginRequest);
		mockMvc.perform(post("/login").contentType("application/json").content(requestBody))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("$.message").value("Invalid credentials."));
	}

	/*
	 * Method used to create LoginRequest object
	 */
	private LoginRequest createLoginRequest() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("admin");
		return loginRequest;
	}
	
}
