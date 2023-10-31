package com.inexture.users.service;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.ResetPasswordRequest;

/**
 * Service for ResetPasswordToken Entity
 */
public interface ResetPasswordTokenService {

	/**
	 * This method generates reset password token for user and send email with link to reset password
	 * 
	 * @param user
	 */
	void generateAndSendToken(User user);

	/**
	 * This method validates token and update new password of user
	 * 
	 * @param resetPasswordRequest
	 * @param response
	 */
	void resetPassword(ResetPasswordRequest resetPasswordRequest, APIResponse response);
}
