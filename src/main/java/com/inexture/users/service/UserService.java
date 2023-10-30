package com.inexture.users.service;

import java.util.List;
import java.util.Optional;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.pojo.UserPojo;

public interface  UserService{

	List<UserPojo> getAll();

	boolean create(UserPojo user);

	long countByUsername(String username);

	long countByUsernameAndIdNot(String username, Integer userId);
	
	long countByEmailAndIdNot(String email, Integer userId);
	
	long countByEmail(String email);

	Optional<LoginResponse> login(LoginRequest loginRequest);

	Optional<UserPojo> getById(Integer userId);

	boolean update(UserPojo userPojo);

	Optional<User> getByUsername(String username);

	boolean deleteById(Integer userId);



}
