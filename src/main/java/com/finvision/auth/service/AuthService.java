package com.finvision.auth.service;

import com.finvision.auth.dto.AuthResponse;
import com.finvision.auth.dto.LoginRequest;
import com.finvision.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}