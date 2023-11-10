package com.inexture.users.config;

import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.utils.ApplicationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class is used to return JSON response on authentication failure so REST client do not receive a default HTML for login failure
 */
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ApplicationUtils applicationUtils;

    /**
     * @see AuthenticationFailureHandler#onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        APIResponse apiResponse = ApplicationUtils.generateResponse(true, "Invalid username or password!");
        response.getOutputStream().print(applicationUtils.generateJsonFromObject(apiResponse));

    }
}
