package com.example.backend.config;

import com.example.backend.dto.response.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointConfig implements org.springframework.security.web.AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public AuthenticationEntryPointConfig(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
            this.delegate.commence(request, response, authException);
            response.setContentType("application/json;charset=UTF-8");

            RestResponse<Object> res = new RestResponse<Object>();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setError(authException.getCause().getMessage());
            res.setMessage("Token không hợp lệ !");

            mapper.writeValue(response.getWriter(), res);
    }
}