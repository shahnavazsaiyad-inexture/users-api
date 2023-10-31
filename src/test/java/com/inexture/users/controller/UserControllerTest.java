package com.inexture.users.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.HttpHeaders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.service.ResetPasswordTokenService;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.utils.JwtUtility;
import com.inexture.users.validator.UpdateUserRequestValidator;
import com.jayway.jsonpath.JsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest{
	
	/**
	 * Test case for List User API success scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testListUsersSuccess() throws Exception {
		
		List<UserPojo> userList = getUsers();
		String token = generateToken("admin", "Admin", "1");

		when(userService.getAll()).thenReturn(userList);
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		MvcResult result = mockMvc.perform(get("/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
										.andExpect(status().isOk())
										.andExpect(jsonPath("$.error").value(false))
										.andReturn();

		String response = result.getResponse().getContentAsString();
		
		List<Object> users = JsonPath.read(response, "$.data");
		assertNotNull(users);
		assertTrue(!users.isEmpty());
	}

	/**
	 * Test Case for List User API user not found scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testListUsersNoUserFound() throws Exception {
		
		String token = generateToken("admin", "Admin", "1");

		when(userService.getAll()).thenReturn(List.of());
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(get("/users").header(HttpHeaders.AUTHORIZATION, "Bearer "+token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("$.message").value("No records found."))				
				.andDo(print());

	}
	
	/**
	 * Test case for List Users API unauthorized user scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testListUsersUnauthorized() throws Exception {
		
		List<UserPojo> userList = getUsers();
		String token = generateToken("user", "User", "2");

		when(userService.getAll()).thenReturn(userList);
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(get("/users").header(HttpHeaders.AUTHORIZATION, "Bearer "+token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("$.message").value("You are not authorized to access this service."))
				.andDo(print());

	}

	/**
	 * Test case for Get User API success scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserSuccess() throws Exception {
		
		String token = generateToken("user", "User", "2");

		UserPojo user = createUser(2);
		
		when(userService.getById(any())).thenReturn(Optional.of(user));
		
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(get("/users/2").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(false))
				.andExpect(jsonPath("$.data").exists());

	}
	
	/**
	 * Test case for Get User API unauthorized user scenario (retrieving details of other user)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserUnauthorized() throws Exception {
		
		String token = generateToken("user", "User", "1");

		UserPojo user = createUser(2);
		
		when(userService.getById(any())).thenReturn(Optional.of(user));
		
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(get("/users/2").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("message").value("You are not authorized to access other users data."));

	}
	
	/**
	 * Test case for Get User API User not found scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUserNotfound() throws Exception {
		
		String token = generateToken("user", "User", "2");

		
		when(userService.getById(any())).thenReturn(Optional.empty());
		
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(get("/users/2").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("message").value("User not found."));

	}
	
	/**
	 * Test case for Delete User API success scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUserSuccess() throws Exception {
		
		String token = generateToken("admin", "Admin", "1");

		
		when(userService.deleteById(anyInt())).thenReturn(true);
		
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(delete("/users/2").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(false))
				.andExpect(jsonPath("$.message").value("User has been deleted."));

	}	
	
	/**
	 * Test case for Delete User API unauthorized user scenario
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUserUnauthorized() throws Exception {
		
		String token = generateToken("user", "User", "2");

		
		when(userService.deleteById(anyInt())).thenReturn(true);
		
		DecodedJWT decodedToken = decodeToken(token);
		
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		mockMvc.perform(delete("/users/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("$.message").value("You are not authorized to delete user."));

	}
	
	
	/**
	 * Test case for success scenario of update user API
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserSuccess() throws Exception {
		
		String token = generateToken("user", "User", "2");
		DecodedJWT decodedToken = decodeToken(token);
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		UserPojo userPojo = createUser(2);
		
		String requestBody = objectMapper.writeValueAsString(userPojo);
		
		when(updateUserRequestValidator.supports(any())).thenReturn(true);

		when(userService.update(any())).thenReturn(true);
		
		mockMvc.perform(post("/users").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).content(requestBody))
		
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(false))
				.andExpect(jsonPath("message").value("User details updates successfully."));
	}
	
	
	/**
	 * Test case for unauthorized user scenario in update user API (updating other user's data!)
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserUnauthorized() throws Exception {
		
		String token = generateToken("user", "User", "2");
		DecodedJWT decodedToken = decodeToken(token);
		when(jwtUtility.decodeToken(any())).thenReturn(decodedToken);
		
		UserPojo userPojo = createUser(1);
		
		String requestBody = objectMapper.writeValueAsString(userPojo);
		
		when(updateUserRequestValidator.supports(any())).thenReturn(true);

		when(userService.update(any())).thenReturn(true);
		
		mockMvc.perform(post("/users").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).content(requestBody))
		
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.error").value(true))
				.andExpect(jsonPath("message").value("You are not authorized to update other users data."));
	}
	
	
	/**
	 * Method used to generate JWT token
	 * 
	 * @param username
	 * @param role
	 * @param userId
	 * @return
	 */
	private String generateToken(String username, String role, String userId) {
		Algorithm algorithm = Algorithm.HMAC256("inexture-solutions");

		String token = JWT.create()
		        .withIssuer("auth0")
		        .withClaim("username", username)
		        .withClaim("role", role)
		        .withClaim("userId", userId)
		        .withExpiresAt(Instant.now().plusSeconds(3600))
		        .sign(algorithm);

		return token;

	}
	

	/**
	 * Method used to decode JWT token
	 * 
	 * @param token
	 * @return
	 */
	private DecodedJWT decodeToken(String token) {
		DecodedJWT decodedJWT = null;
	    Algorithm algorithm = Algorithm.HMAC256("inexture-solutions");
	    JWTVerifier verifier = JWT.require(algorithm)
			    		.withIssuer("auth0")
			    		.build();
	        
	    decodedJWT = verifier.verify(token);

		return decodedJWT;
	}

	/**
	 * Method used to generate user list
	 * 
	 * @return
	 */
	private List<UserPojo> getUsers() {
		List<UserPojo> users = new ArrayList<>();
		for(int i=1;i<11; i++) {
			users.add(createUser(i));
		}
		return users;
	}
	
	/**
	 * Method used to generate UserPojo object
	 * 
	 * @param id
	 * @return
	 */
	private UserPojo createUser(Integer id) {

		UserPojo userPojo = new UserPojo();
		userPojo.setId(id);
		userPojo.setUsername("testuser");
		userPojo.setEmail("testuser@gmail.com");
		userPojo.setFirstName("test");
		userPojo.setLastName("test");
		userPojo.setRole("User");
		userPojo.setAddresses(new ArrayList<>());
		AddressPojo address = new AddressPojo();
		address.setStreet("Test Street");
		address.setCity("Test");
		address.setPincode("000000");
		address.setPincode("396580");
		userPojo.getAddresses().add(address);
		return userPojo;
	}
}
