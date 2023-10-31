package com.inexture.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inexture.users.service.ResetPasswordTokenService;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.utils.JwtUtility;
import com.inexture.users.validator.LoginRequestValidator;
import com.inexture.users.validator.RegistrationRequestValidator;
import com.inexture.users.validator.UpdateUserRequestValidator;

class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	
	@MockBean
	protected UserService userService;
	
	@MockBean
	protected ApplicationUtils applicationUtils;
	
	@MockBean
	protected LoginRequestValidator loginRequestValidator;
	
	@MockBean
	protected JwtUtility jwtUtility;
	
	@MockBean
	protected ResetPasswordTokenService resetPasswordTokenService;
	
	
	@MockBean
	protected RegistrationRequestValidator registrationRequestValidator;
	
	@MockBean
	protected UpdateUserRequestValidator updateUserRequestValidator;


}
