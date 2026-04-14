package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskDto {

	@NotBlank(message = "Title is required")
	private String title;

	private String description;

	private String status;

	private String priority;

	private UUID assigneeId;

	private LocalDate dueDate;
}
