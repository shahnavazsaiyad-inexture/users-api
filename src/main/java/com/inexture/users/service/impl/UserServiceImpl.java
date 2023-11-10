package com.inexture.users.service.impl;

import java.security.MessageDigest;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.inexture.users.entity.Address;
import com.inexture.users.entity.User;
import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is service implementation for UserService
 */
@Service
@Transactional
@Slf4j
public class UserServiceImpl extends BaseService implements UserService, UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	/**
	 * @see UserService#getAll()
	 */
	@Override
	public List<UserPojo> getAll() {
		List<User> userEntities = userRepository.findAll();
		if(!ApplicationUtils.isEmpty(userEntities)) {
			List<UserPojo> users = userEntities.stream().map(this::entityToPojo).toList();	
			return users;
		}
		return List.of();
	}
	
	/**
	 * @see UserService#getById(Integer)
	 */
	@Override
	public Optional<UserPojo> getById(Integer userId) {

		Optional<User> userEntity = userRepository.findById(userId);
		if(userEntity.isPresent()) {
			UserPojo user = entityToPojo(userEntity.get());
			return Optional.of(user);
		}
		return Optional.empty();
	}
	
	/**
	 * @see UserService#getByUsername(String)
	 */
	@Override
	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * @see UserService#create(UserPojo)
	 */
	@Override
	public boolean create(UserPojo userPojo) {
		try {
			String hashedPassword = applicationUtils.hashPassword(userPojo.getPassword());
			userPojo.setPassword(hashedPassword);
			
			User user = userPojo.toEntity();
			User savedUser = userRepository.save(user);
			
			if(!Objects.isNull(savedUser)) {
				return true;
			}
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * @see UserService#login(LoginRequest)
	 */
	@Override
	public Optional<LoginResponse> login(LoginRequest loginRequest) {

		try {
//			String encodedPassword = applicationUtils.hashPassword(loginRequest.getPassword());
//			loginRequest.setPassword(encodedPassword);
			
			Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());

			if(user.isPresent()
					&& passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
				
				String token = "";//jwtUtility.createToken(user.get().getUsername(), user.get().getRole(), user.get().getId().toString());

				LoginResponse loginResponse = LoginResponse.builder()
														.userId(user.get().getId())
														.username(user.get().getUsername())
														.role(user.get().getRole())
														.jwtToken(token)
														.build();
				return Optional.of(loginResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}


	/**
	 * @see UserService#update(UserPojo)
	 */
	@Override
	public boolean update(UserPojo userPojo) {
		try {
			
			Optional<User> userOptional = userRepository.findById(userPojo.getId());
			if(userOptional.isPresent()) {
				
				User user = userOptional.get();
				BeanUtils.copyProperties(userPojo, user, "password");
				
				Iterator<Address> itrAddress = user.getAddresses().iterator();
				
				while(itrAddress.hasNext()) {
					Address address = itrAddress.next();
					Optional<AddressPojo> findAny = userPojo.getAddresses().stream().filter(a -> (!Objects.isNull(a.getId()) && a.getId().equals(address.getId()))).findAny(); 
					
					if(findAny.isPresent()) {

						BeanUtils.copyProperties(findAny.get(), address);
					}else {

						addressRepository.deleteById(address.getId());
						itrAddress.remove();
					}
				}

				userPojo.getAddresses().stream().filter(a -> (Objects.isNull(a.getId()) || a.getId().equals(0)))
						.forEach(a -> {
							Address address = a.toEntity();
							address.setUser(user);
							user.getAddresses().add(address);
							});
				
				User savedUser = userRepository.save(user);
				
				if(!Objects.isNull(savedUser)) {
					return true;
				}
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}	

	/**
	 * @see UserService#deleteById(Integer)
	 */
	@Override
	public boolean deleteById(Integer userId) {
		try{
			userRepository.deleteById(userId);
			return true;
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * @see UserService#countByUsername(String)
	 */
	@Override
	public long countByUsername(String username) {
		return userRepository.countByUsername(username);
	}
	
	/**
	 * @see UserService#countByEmail(String)
	 */
	@Override
	public long countByEmail(String email) {
		return userRepository.countByEmail(email);
	}

	/**
	 * @see UserService#countByUsernameAndIdNot(String, Integer)
	 */
	@Override
	public long countByUsernameAndIdNot(String username, Integer userId) {
		return userRepository.countByUsernameAndIdNot(username, userId);
	}

	/**
	 * @see UserService#countByEmailAndIdNot(String, Integer)
	 */
	@Override
	public long countByEmailAndIdNot(String email, Integer userId) {
		return userRepository.countByEmailAndIdNot(email, userId);
	}

	/**
	 * This method is used to convert User Entity to UserPojo
	 * 
	 * @param user
	 * @return
	 */
	private UserPojo entityToPojo(User user) {
		if(Objects.isNull(user)) {
			return null;
		}
		UserPojo userPojo = new UserPojo();
		BeanUtils.copyProperties(user, userPojo, "password");
		
		if(!ApplicationUtils.isEmpty(user.getAddresses())) {
			List<AddressPojo> addresses = user.getAddresses().stream().map(address -> {
				AddressPojo addressPojo = new AddressPojo();
				BeanUtils.copyProperties(address, addressPojo);
				return addressPojo;
			}).toList();
			userPojo.setAddresses(addresses);
		}
		return userPojo;
	}

	/**
	 * @see UserDetailsService#loadUserByUsername(String) 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()){
			throw new UsernameNotFoundException("User does not exists!");
		}
		return new org.springframework.security.core.userdetails.User(user.get().getUsername(),user.get().getPassword(), Set.of(new SimpleGrantedAuthority(user.get().getRole())));
	}
}
