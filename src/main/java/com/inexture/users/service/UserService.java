package com.inexture.users.service;

import java.util.List;
import java.util.Optional;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.pojo.UserPojo;

/**
 * Service for User Entity
 */
public interface  UserService{

	/**
	 * This method retrieves all users from database
	 * @return
	 */
	List<UserPojo> getAll();

	/**
	 * This method creates a new user
	 * 
	 * @param user
	 * @return
	 */
	boolean create(UserPojo user);

	/**
	 * This method returns count of user by username
	 * 
	 * @param username
	 * @return
	 */
	long countByUsername(String username);

	/**
	 * This method returns count of user where username matches but userId does not match
	 * 
	 * @param username
	 * @param userId
	 * @return
	 */
	long countByUsernameAndIdNot(String username, Integer userId);
	
	/**
	 * This method returns count of user where email matches but userId does not match
	 * 
	 * @param email
	 * @param userId
	 * @return
	 */
	long countByEmailAndIdNot(String email, Integer userId);
	
	/**
	 * This method returns count of user by email
	 * 
	 * @param email
	 * @return
	 */
	long countByEmail(String email);

	/**
	 * This method is used to verify login credentials and generate LoginResponse
	 * 
	 * @param loginRequest
	 * @return
	 */
	Optional<LoginResponse> login(LoginRequest loginRequest);

	/**
	 * This method is used to retrieve User by userId
	 * @param userId
	 * @return
	 */
	Optional<UserPojo> getById(Integer userId);

	/**
	 * This method is used to update a user
	 * 
	 * @param userPojo
	 * @return
	 */
	boolean update(UserPojo userPojo);

	/**
	 * This method is used to return user by username
	 * 
	 * @param username
	 * @return
	 */
	Optional<User> getByUsername(String username);

	/**
	 * This method is used to delete user by userId
	 * 
	 * @param userId
	 * @return
	 */
	boolean deleteById(Integer userId);



}
