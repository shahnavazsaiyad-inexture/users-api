package com.inexture.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inexture.users.entity.ResetPasswordToken;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Integer> {

	Optional<ResetPasswordToken> findByToken(String token);

}
