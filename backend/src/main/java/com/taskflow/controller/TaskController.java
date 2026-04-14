package com.taskflow.controller;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.taskflow.dto.TaskDto;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping("/projects/{id}/tasks")
	public ResponseEntity<?> getTasks(@PathVariable UUID id, @RequestParam(required = false) String status,
			@RequestParam(required = false) UUID assignee, @AuthenticationPrincipal UUID userId) {

		return ResponseEntity.ok(taskService.getTasks(id, status, assignee, userId));
	}

	@PostMapping("/projects/{id}/tasks")
	public ResponseEntity<?> create(@PathVariable UUID id, @Valid @RequestBody TaskDto dto,
			@AuthenticationPrincipal UUID userId) {

		return ResponseEntity.status(201).body(taskService.createTask(id, dto, userId));
	}

	@PatchMapping("/tasks/{id}")
	public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody TaskDto dto,
			@AuthenticationPrincipal UUID userId) {

		return ResponseEntity.ok(taskService.updateTask(id, dto, userId));
	}

	@DeleteMapping("/tasks/{id}")
	public ResponseEntity<?> delete(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
		taskService.deleteTask(id, userId);
		return ResponseEntity.noContent().build();
	}
}
