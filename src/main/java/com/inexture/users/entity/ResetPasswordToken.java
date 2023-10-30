package com.inexture.users.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name =  "reset_password_token")
@Getter @Setter @NoArgsConstructor
public class ResetPasswordToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "token_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;	

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "token")
	private String token;
	
	@Column(name = "is_used")
	private Boolean isUsed;
	
	@Column(name = "valid_till")
	private LocalDateTime validTill;
}
