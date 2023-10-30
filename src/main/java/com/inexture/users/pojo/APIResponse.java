package com.inexture.users.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(value = Include.NON_NULL)
public class APIResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean isError;
	private String message;
	private Object data;
}
