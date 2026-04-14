package com.taskflow.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {

	@Id
	private UUID id;
	private String title;
	private String description;
	private String status;
	private String priority;
	@Column(name = "project_id")
	private UUID projectId;
	@Column(name = "assignee_id")
	private UUID assigneeId;
	@Column(name = "due_date")
	private LocalDate dueDate;
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
