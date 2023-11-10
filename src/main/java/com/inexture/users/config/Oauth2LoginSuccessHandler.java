package com.inexture.users.config;

import com.inexture.users.entity.User;
import com.inexture.users.pojo.APIResponse;
import com.inexture.users.pojo.LoginResponse;
import com.inexture.users.service.UserService;
import com.inexture.users.utils.ApplicationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class Oauth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationUtils applicationUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String login;

        if(authentication.getPrincipal() instanceof  OAuth2User){
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            login = oauthUser.getAttribute("login").toString();
        }else {
            org.springframework.security.core.userdetails.User securityUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            login = securityUser.getUsername();
        }
        this.setAlwaysUseDefaultTargetUrl(true);
        if(login != null){
            Optional<User> systemUser = userService.getByUsername(login.toString());
            if(authentication.getPrincipal() instanceof  OAuth2User) {

                if (systemUser.isPresent()) {

                    this.setDefaultTargetUrl("http://localhost:3000");
                } else {
                    this.setDefaultTargetUrl("http://localhost:3000/register/github/" + login.toString());
                }
            }else{
                if (systemUser.isPresent()) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    LoginResponse loginResponse = LoginResponse.builder().userId(systemUser.get().getId()).username(systemUser.get().getUsername()).role(systemUser.get().getRole()).build();
                    APIResponse apiResponse = ApplicationUtils.generateResponse(false, loginResponse);
                    response.getWriter().println(applicationUtils.generateJsonFromObject(apiResponse));
                    return;
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
