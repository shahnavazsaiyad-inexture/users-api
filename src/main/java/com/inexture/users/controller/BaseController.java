package com.inexture.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.inexture.users.service.ResetPasswordTokenService;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;

/**
 * This class is a base class for all controllers which holds common dependencies.
 */
@Controller
public abstract class BaseController {

	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected ApplicationUtils applicationUtils;
	
	@Autowired
	protected ResetPasswordTokenService resetPasswordTokenService;
}
