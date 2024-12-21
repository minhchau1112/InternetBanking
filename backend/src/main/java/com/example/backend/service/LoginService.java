package com.example.backend.service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
}
