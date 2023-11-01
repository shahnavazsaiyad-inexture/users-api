package com.inexture.users.pojo;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.inexture.users.entity.User;
import com.inexture.users.utils.ApplicationUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
@JsonInclude(value = Include.NON_NULL)
public class UserPojo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String role;
	private String password;
	private String confirmPassword;

	private List<AddressPojo> addresses;

	public User toEntity() {
		
		User user = new User();
		BeanUtils.copyProperties(this, user);
		
		if(!ApplicationUtils.isEmpty(addresses)) {
			user.setAddresses(addresses.stream().map(AddressPojo::toEntity)
					.map(address -> {
						address.setUser(user);
						return address;
						})
					.toList());
		}
		return user;
	}
}
