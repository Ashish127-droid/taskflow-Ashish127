package com.taskflow.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	private UUID id;

	private String name;

	@Column(unique = true)
	private String email;

	@JsonIgnore
	private String password;

	private LocalDateTime createdAt;

}
