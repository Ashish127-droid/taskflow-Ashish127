package com.taskflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

	@NotBlank
	private String name;

	@Email
	private String email;

	@Size(min = 6)
	private String password;
}
