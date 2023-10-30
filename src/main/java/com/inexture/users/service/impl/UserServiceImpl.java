package com.inexture.users.service.impl;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inexture.users.entity.Address;
import com.inexture.users.entity.User;
import com.inexture.users.pojo.AddressPojo;
import com.inexture.users.pojo.LoginRequest;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.pojo.UserPojo;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
import com.inexture.users.utils.JwtUtility;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl extends BaseService implements UserService {
	

	@Autowired
	private JwtUtility jwtUtility;

	@Override
	public List<UserPojo> getAll() {
		List<User> userEntities = userRepository.findAll();
		if(!ApplicationUtils.isEmpty(userEntities)) {
			List<UserPojo> users = userEntities.stream().map(this::entityToPojo).toList();	
			return users;
		}
		return List.of();
	}
	

	@Override
	public Optional<UserPojo> getById(Integer userId) {

		Optional<User> userEntity = userRepository.findById(userId);
		if(userEntity.isPresent()) {
			UserPojo user = entityToPojo(userEntity.get());
			return Optional.of(user);
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public boolean create(UserPojo userPojo) {
		try {
			String hashedPassword = ApplicationUtils.hashPassword(userPojo.getPassword());
			userPojo.setPassword(hashedPassword);
			
			User user = userPojo.toEntity();
			User savedUser = userRepository.save(user);
			
			if(!Objects.isNull(savedUser)) {
				return true;
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	@Override
	public Optional<LoginResponse> login(LoginRequest loginRequest) {

		try {
			String encodedPassword = ApplicationUtils.hashPassword(loginRequest.getPassword());
			loginRequest.setPassword(encodedPassword);
			
			Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());

			if(user.isPresent()
					&& MessageDigest.isEqual(loginRequest.getPassword().getBytes(), user.get().getPassword().getBytes())) {
				
				String token = jwtUtility.createToken(user.get().getUsername(), user.get().getRole(), user.get().getId().toString());

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

	@Override
	public long countByUsername(String username) {
		return userRepository.countByUsername(username);
	}
	

	@Override
	public long countByEmail(String email) {
		return userRepository.countByEmail(email);
	}


	@Override
	public long countByUsernameAndIdNot(String username, Integer userId) {
		return userRepository.countByUsernameAndIdNot(username, userId);
	}


	@Override
	public long countByEmailAndIdNot(String email, Integer userId) {
		return userRepository.countByEmailAndIdNot(email, userId);
	}

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


}
