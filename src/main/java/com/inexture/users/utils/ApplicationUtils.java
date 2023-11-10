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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inexture.users.pojo.APIResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * This class provide utility methods for application
 */
@Component
@Slf4j
public class ApplicationUtils {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * This method is used to generate a Json String from object
	 * 
	 * @param obj
	 * @return
	 */
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
	
	/**
	 * This method is used to generate object from Json String
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
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
	
	/**
	 * This method is used to generate error API response from BindingResult object
	 * 
	 * @param bindingResult
	 * @return
	 */
	public ResponseEntity<APIResponse> generateErrorResponse(BindingResult bindingResult) {
	
		Map<String, String> errorMap = new HashMap<>();		
		
		bindingResult.getFieldErrors().forEach(error -> {
			errorMap.put(error.getField(), messageSource.getMessage(error.getCode(), new String[] {error.getField()}, Locale.getDefault()));
		});
		
		return ResponseEntity.ok(APIResponse.builder().isError(true).message("Validation Error!").data(errorMap).build());
	}
	
	/**
	 * This method is used to check if a collection is null or empty
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection collection) {
		return Objects.isNull(collection) || collection.isEmpty();
	}

	/**
	 * This method is used to generate API response with all Fields
	 * 
	 * @param isError
	 * @param message
	 * @param data
	 * @return
	 */
	public static APIResponse generateResponse(boolean isError, String message, Object data) {
		
		return APIResponse.builder().message(message).data(data).isError(isError).build();
	}
	
	/**
	 * This method is used to generate API response with isError and data field
	 * 
	 * @param isError
	 * @param data
	 * @return
	 */
	public static APIResponse generateResponse(boolean isError, Object data) {
		
		return APIResponse.builder().data(data).isError(isError).build();
	}
	
	/**
	 * This method is used to generate API response with isError and message field
	 * 
	 * @param isError
	 * @param message
	 * @return
	 */
	public static APIResponse generateResponse(boolean isError, String message) {
		
		return APIResponse.builder().message(message).isError(isError).build();
	}

	/**
	 * This method is used to hash a password
	 * 
	 * @param plainText
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String hashPassword(String plainText) throws NoSuchAlgorithmException {
		return passwordEncoder.encode(plainText);
	}

	/**
	 * This method is used to check if a string is null or empty
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isEmpty(String text) {
		return Objects.isNull(text) || text.isEmpty();
	}
	
}
