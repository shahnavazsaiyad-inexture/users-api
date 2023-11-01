package com.inexture.users.pojo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.inexture.users.entity.Address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class AddressPojo implements Serializable{


	private static final long serialVersionUID = 1L;

	private Integer id;
	private String street;
	private String city;
	private String state;
	private String country;
	private Integer pincode;
	
	public Address toEntity() {
		Address address = new Address();
		BeanUtils.copyProperties(this, address);
		return address;
	}
}

