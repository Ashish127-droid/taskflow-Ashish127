package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data

public class Project {

	@Id
	private UUID id;

	@Column(nullable = false)
	private String name;

	private String description;

	@Column(name = "owner_id", nullable = false)
	private UUID ownerId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
