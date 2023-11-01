package com.inexture.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.UserPojo;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest extends BaseControllerTest{

	
	/**
	 * Test case for success scenario of registration API
	 * @throws Exception
	 */
	@Test
	public void testRegisterSuccess() throws Exception {
		
		UserPojo userPojo = createUser();
		
		String requestBody = objectMapper.writeValueAsString(userPojo);
		
		when(registrationRequestValidator.supports(any())).thenReturn(true);

		when(userService.create(any())).thenReturn(true);
		
		mockMvc.perform(post("/register").contentType("application/json").content(requestBody))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(false))
				.andExpect(jsonPath("message").value("Congratulations! You are successfully registered!"));
		
	}
	

	/**
	 * Test case for error scenario of registration API
	 * @throws Exception
	 */
	@Test
	public void testRegisterError() throws Exception {
		UserPojo userPojo = createUser();
		String requestBody = objectMapper.writeValueAsString(userPojo);
		
		when(registrationRequestValidator.supports(any())).thenReturn(true);
		
		when(userService.create(any())).thenThrow(new RuntimeException("Runtime failure"));
		
		mockMvc.perform(post("/register").contentType("application/json").content(requestBody))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("$.message").value("Something is wrong, contact support."));		
	}

	/**
	 * Method used to create a UserPojo object
	 * @return
	 */
	private UserPojo createUser() {

		UserPojo userPojo = new UserPojo();
		userPojo.setUsername("testuser");
		userPojo.setEmail("testuser@gmail.com");
		userPojo.setFirstName("test");
		userPojo.setLastName("test");
		userPojo.setRole("User");
		userPojo.setAddresses(new ArrayList<>());
		AddressPojo address = new AddressPojo();
		address.setStreet("Test Street");
		address.setCity("Test");
		address.setPincode(380000);
		userPojo.getAddresses().add(address);
		return userPojo;
	}
}
