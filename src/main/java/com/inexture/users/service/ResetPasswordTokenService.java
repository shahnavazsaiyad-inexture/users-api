package com.inexture.users.service;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.ResetPasswordRequest;

public interface ResetPasswordTokenService {

	void generateAndSendToken(User user);

	void resetPassword(ResetPasswordRequest resetPasswordRequest, APIResponse response);
}
