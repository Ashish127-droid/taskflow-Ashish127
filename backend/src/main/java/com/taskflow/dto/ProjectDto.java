package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDto {

	@NotBlank
	private String name;

	private String description;
}
