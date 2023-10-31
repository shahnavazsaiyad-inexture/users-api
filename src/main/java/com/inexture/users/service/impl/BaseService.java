package com.inexture.users.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inexture.users.repository.AddressRepository;
import com.inexture.users.repository.ResetPasswordTokenRepository;
import com.inexture.users.repository.UserRepository;
import com.inexture.users.utils.ApplicationUtils;

/**
 * Base class for all services that holds common dependencies and repositories
 */
@Service
public abstract class BaseService {

	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected AddressRepository addressRepository;
	
	@Autowired
	protected ResetPasswordTokenRepository resetPasswordTokenRepository;
	
	@Autowired
	protected ApplicationUtils applicationUtils;
}
