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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taskflow.dto.ProjectDto;
import com.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
	private final ProjectService projectService;

	@GetMapping
	public ResponseEntity<?> list(@AuthenticationPrincipal UUID userId) {
		return ResponseEntity.ok(projectService.getUserProjects(userId));
	}

	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody ProjectDto dto, @AuthenticationPrincipal UUID userId) {
		return ResponseEntity.status(201).body(projectService.createProject(dto, userId));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProjectDto dto,
			@AuthenticationPrincipal UUID userId) {
		return ResponseEntity.ok(projectService.updateProject(id, dto, userId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
		projectService.deleteProject(id, userId);
		return ResponseEntity.noContent().build();
	}
}
