package com.inexture.users.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.inexture.users.entity.ResetPasswordToken;
import com.inexture.users.entity.User;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.ResetPasswordRequest;
import com.inexture.users.service.ResetPasswordTokenService;
import com.inexture.users.utils.ApplicationUtils;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provide Service implementation for ResetPasswordTokenService
 */
@Service
@Slf4j
@Transactional
public class ResetPasswordTokenServiceImpl extends BaseService implements ResetPasswordTokenService {

	@Autowired
	private JavaMailSenderImpl javaMailSender;
	
	@Value("${reset.password.url}")
	private String resetPasswordUrl;
	
	@Value("${mail.send.from}")
	private String mailSendFrom;
	
	/**
	 * @see ResetPasswordTokenService#generateAndSendToken(User)
	 */
	@Override
	public void generateAndSendToken(User user) {
		try {
			
			String token = UUID.randomUUID().toString();
			ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
			resetPasswordToken.setToken(token);
			resetPasswordToken.setUser(user);
			resetPasswordToken.setValidTill(LocalDateTime.now().plusHours(1));
			resetPasswordToken.setIsUsed(false);
			resetPasswordTokenRepository.save(resetPasswordToken);
			
			sendResetPasswordEmail(user.getEmail(), token);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}	
	}
	
	/**
	 * This method is used to send an Email to user with reset password link
	 */
	private void sendResetPasswordEmail(String email, String token) {
		String resetUrl = resetPasswordUrl+token;
		
		try {
			File file = ResourceUtils.getFile("classpath:static/email-templates/reset-password-email.html");
			byte[] fileBytes = Files.readAllBytes(file.toPath());
			String emailBody = new String(fileBytes);

			emailBody = emailBody.replace("[[RESET_PASSWORD_LINK]]", resetUrl);
			String emailSubject = "Reset your password";
			
			MimeMessage message = javaMailSender.createMimeMessage();
			message.setFrom(mailSendFrom);
			message.addRecipient(RecipientType.TO, new InternetAddress(email));
			message.setSubject(emailSubject);
			message.setText(emailBody, "utf-8", "html");
			
			javaMailSender.send(message);
			
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @see ResetPasswordTokenService#resetPassword(ResetPasswordRequest, APIResponse)
	 */
	@Override
	public void resetPassword(ResetPasswordRequest resetPasswordRequest, APIResponse response) {
		try {
			Optional<ResetPasswordToken> token = resetPasswordTokenRepository.findByToken(resetPasswordRequest.getToken());
			if(token.isPresent()) {
				if(!token.get().getIsUsed() && token.get().getValidTill().isAfter(LocalDateTime.now())) {
					
					String encodedPassword = applicationUtils.hashPassword(resetPasswordRequest.getNewPassword());
					User user = token.get().getUser();
					user.setPassword(encodedPassword);
					userRepository.save(user);
					
					token.get().setIsUsed(true);
					resetPasswordTokenRepository.save(token.get());
					response.setError(false);
					response.setMessage("Password has been changed!");
					return;
				}
				
			}
			response.setError(true);
			response.setMessage("Link has been expired!");
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			response.setError(true);
			response.setMessage("Something is wrong contact support!");
		}
		
	}
}
