package com.inexture.users.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ResetPasswordRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String newPassword;
	private String token;
}