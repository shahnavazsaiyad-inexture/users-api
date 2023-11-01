package com.inexture.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.inexture.users.entity.Address;
import com.inexture.users.entity.User;
import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.repository.UserRepository;
import com.inexture.users.service.UserService;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	/**
	 * Test case for login success scenario
	 */
	@Test
	void testLoginSuccess() {
		User user = createUser();
		when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
		
		LoginRequest loginRequest = createLoginRequest();
		Optional<LoginResponse> response = userService.login(loginRequest);
		assertTrue(response.isPresent());
		assertNotNull(response.get().getJwtToken());
	}

	/**
	 * Test case for login failure scenario
	 */
	@Test
	void testLoginFailure() {
		User user = createUser();
		when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
		
		LoginRequest loginRequest = createLoginRequest();
		loginRequest.setPassword("wrongpassword");
		Optional<LoginResponse> response = userService.login(loginRequest);
		assertFalse(response.isPresent());
	}
	
	/**
	 * Test case for get All user success scenario
	 */
	@Test
	void testGetAllSuccess() {
		List<User> users = createUsers();
		when(userRepository.findAll()).thenReturn(users);
		
		List<UserPojo> userPojos = userService.getAll();
		
		assertNotNull(userPojos);
		
		assertFalse(userPojos.isEmpty());
		
	}
	
	/**
	 * Test case for get All user empty list return scenario
	 */
	@Test
	void testGetAllEmpty() {

		when(userRepository.findAll()).thenReturn(List.of());
		
		List<UserPojo> userPojos = userService.getAll();
		
		assertNotNull(userPojos);
		
		assertTrue(userPojos.isEmpty());
		
	}
	
	/**
	 * Test case for getById method with success scenario
	 */
	@Test
	void testGetByIdSuccess() {
		
		when(userRepository.findById(anyInt())).thenReturn(Optional.of(createUser()));
		
		Optional<UserPojo> userPojo = userService.getById(1);
		
		assertTrue(userPojo.isPresent());
		
	}
	
	/**
	 * Test case for getById method with empty user scenario
	 */
	@Test
	void testGetByIdEmpty() {
		
		when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		Optional<UserPojo> userPojo = userService.getById(1);
		
		assertTrue(userPojo.isEmpty());
		
	}
	
	/**
	 * Test case for getByUsername method with success scenario
	 */
	@Test
	void testGetByUsernameSuccess() {
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(createUser()));
		
		Optional<User> user = userService.getByUsername("user");
		
		assertTrue(user.isPresent());
		
	}
	
	/**
	 * Test case for getByUsername method with empty scenario
	 */
	@Test
	void testGetByUsernameEmpty() {
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		Optional<User> user = userService.getByUsername("user");
		
		assertTrue(user.isEmpty());
		
	}
	
	/**
	 * Test case for create method with success scenario
	 */
	@Test
	void testCreateSuccess() {
		
		when(userRepository.save(any())).thenReturn(createUser());
		boolean created = userService.create(createUserPojo());
		assertTrue(created);
	}
	
	/**
	 * Test case for create method with error scenario
	 */
	@Test
	void testCreateError() {
		
		when(userRepository.save(any())).thenThrow(new RuntimeException("Some exception!"));
		boolean created = userService.create(createUserPojo());
		assertFalse(created);
	}
	
	private UserPojo createUserPojo() {
		
		UserPojo userPojo = new UserPojo();
		userPojo.setUsername("testuser");
		userPojo.setEmail("testuser@gmail.com");
		userPojo.setFirstName("test");
		userPojo.setLastName("test");
		userPojo.setPassword("test@123");
		userPojo.setRole("User");
		userPojo.setAddresses(new ArrayList<>());
		AddressPojo address = new AddressPojo();
		address.setStreet("Test Street");
		address.setCity("Test");
		address.setPincode(380000);
		userPojo.getAddresses().add(address);
		return userPojo;
	}

	private List<User> createUsers() {
		List<User> users = new ArrayList<>();
		for(int i=1; i<11; i++) {
			User user = new User();
			user.setAddresses(new ArrayList<Address>());
			user.setId(i);
			user.setFirstName("Test");
			user.setLastName("Test");
			user.setUsername("testuser");
			user.setEmail("test@gmail.com");
			
			Address address = new Address();
			address.setStreet("Teststreet");
			address.setCity("testcity");
			address.setPincode(380000);
			user.getAddresses().add(address);
			users.add(user);
		}
		return users;
	}

	private LoginRequest createLoginRequest() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("admin");
		return loginRequest;
	}

	private User createUser() {
		User user = new User();
		user.setId(1);
		user.setUsername("admin");
		user.setRole("Admin");
		user.setPassword("�iv�A��\b�M�߱g��s�K��o*�H�");
		return user;
	}

}
