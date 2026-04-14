package com.taskflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taskflow.dto.LoginDto;
import com.taskflow.dto.RegisterDto;
import com.taskflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDto dto) {
		return ResponseEntity.status(201).body(authService.register(dto));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginDto dto) {
		return ResponseEntity.ok(authService.login(dto));
	}
}
