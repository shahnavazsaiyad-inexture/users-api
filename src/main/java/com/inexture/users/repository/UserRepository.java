package com.inexture.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inexture.users.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);
	
	long countByUsername(String username);
	long countByEmail(String email);

	long countByUsernameAndIdNot(String username, Integer userId);

	long countByEmailAndIdNot(String email, Integer userId);

}
