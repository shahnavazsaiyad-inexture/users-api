package com.inexture.users.pojo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String role;
	private String jwtToken;
	private Integer userId;
}
