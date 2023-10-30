package com.inexture.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inexture.users.entity.Address;


public interface AddressRepository extends JpaRepository<Address, Integer> {

	
}
