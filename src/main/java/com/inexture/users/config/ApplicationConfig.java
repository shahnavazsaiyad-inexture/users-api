package com.inexture.users.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * This class provide Configuration beans for application
 * 
 */
@Configuration
public class ApplicationConfig {

	@Autowired
	private Environment environment;
	
	/**
	 * MessageSource bean for message.properties
	 * 
	 * @return 
	 */
	@Bean
	public MessageSource messageSource() {
	    ReloadableResourceBundleMessageSource messageSource
	      = new ReloadableResourceBundleMessageSource();
	    
	    messageSource.setBasename("classpath:messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	/**
	 * JavaMailSender bean which is used to send email
	 * 
	 * @return 
	 */
	@Bean
	public JavaMailSenderImpl javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(environment.getProperty("smtp.host"));
	    mailSender.setPort(Integer.parseInt(environment.getProperty("smtp.port")));
	    
	    mailSender.setUsername(environment.getProperty("smtp.username"));
	    mailSender.setPassword(environment.getProperty("smtp.password"));
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", environment.getProperty("mail.transport.protocol"));
	    props.put("mail.smtp.auth", environment.getProperty("mail.smtp.auth"));
	    props.put("mail.smtp.starttls.enable", environment.getProperty("mail.smtp.starttls.enable"));
	    props.put("mail.debug", environment.getProperty("mail.debug"));
	    
	    return mailSender;
	}
	
	/**
	 * corsFilter bean to alow CrossOrigin requests
	 * @return
	 */
	@Bean
	public CorsFilter corsFilter() {

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("OPTIONS");
	    config.addAllowedMethod("HEAD");
	    config.addAllowedMethod("GET");
	    config.addAllowedMethod("PUT");
	    config.addAllowedMethod("POST");
	    config.addAllowedMethod("DELETE");
	    config.addAllowedMethod("PATCH");
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}
}
