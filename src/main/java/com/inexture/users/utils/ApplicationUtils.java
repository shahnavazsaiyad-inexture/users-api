package com.inexture.users.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inexture.users.pojo.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApplicationUtils {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ObjectMapper objectMapper;

	public String generateJsonFromObject(Object obj) {
		String jsonString = null;
		try {
			if(!Objects.isNull(obj)) {
				jsonString = objectMapper.writeValueAsString(obj);			
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return jsonString;
	}
	
	public Object generateObjectFromJson(String jsonString, Class clazz) {
		Object obj = null;
		try {
			if(ApplicationUtils.isEmpty(jsonString)) {
				obj = objectMapper.readValue(jsonString, clazz);
			}
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return jsonString;
	}
	
	
	public ResponseEntity<APIResponse> generateErrorResponse(BindingResult bindingResult) {
	
		Map<String, String> errorMap = new HashMap<>();		
		
		bindingResult.getFieldErrors().forEach(error -> {
			errorMap.put(error.getField(), messageSource.getMessage(error.getCode(), new String[] {error.getField()}, Locale.getDefault()));
		});
		
		return ResponseEntity.ok(APIResponse.builder().isError(true).message("Validation Error!").data(errorMap).build());
	}
	
	
	
	public static boolean isEmpty(Collection collection) {
		return Objects.isNull(collection) || collection.isEmpty();
	}

	public static APIResponse generateResponse(boolean isError, String message, Object data) {
		
		return APIResponse.builder().message(message).data(data).isError(isError).build();
	}
	
	public static APIResponse generateResponse(boolean isError, Object data) {
		
		return APIResponse.builder().data(data).isError(isError).build();
	}
	

	public static APIResponse generateResponse(boolean isError, String message) {
		
		return APIResponse.builder().message(message).isError(isError).build();
	}

	public static String hashPassword(String plainText) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		return new String(messageDigest.digest(plainText.getBytes()));
	}

	public static boolean isEmpty(String text) {
		return Objects.isNull(text) || text.isEmpty();
	}
	
}
