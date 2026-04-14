package com.taskflow.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.dto.LoginDto;
import com.taskflow.dto.RegisterDto;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	 public Map<String, Object> register(RegisterDto dto) {

	        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
	            throw new RuntimeException("Email already exists");
	        }

	        User user = new User();
	        user.setId(UUID.randomUUID());
	        user.setName(dto.getName());
	        user.setEmail(dto.getEmail());
	        user.setPassword(passwordEncoder.encode(dto.getPassword()));
	        user.setCreatedAt(LocalDateTime.now());

	        userRepository.save(user);

	        String token = jwtUtil.generateToken(user);

	        return Map.of(
	                "token", token,
	                "user", user
	        );
	    }

	    public Map<String, Object> login(LoginDto dto) {

	        User user = userRepository.findByEmail(dto.getEmail())
	                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

	        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
	            throw new RuntimeException("Invalid credentials");
	        }

	        String token = jwtUtil.generateToken(user);

	        return Map.of(
	                "token", token,
	                "user", user
	        );
	    }
	}
