package com.taskflow.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.taskflow.dto.ProjectDto;
import com.taskflow.entity.Project;
import com.taskflow.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;

	public List<Project> getUserProjects(UUID userId) {
		return projectRepository.findUserProjects(userId);
	}

	public Project createProject(ProjectDto dto, UUID userId) {
		Project project = new Project();
		project.setId(UUID.randomUUID());
		project.setName(dto.getName());
		project.setDescription(dto.getDescription());
		project.setOwnerId(userId);
		project.setCreatedAt(LocalDateTime.now());

		return projectRepository.save(project);
	}

	public Project updateProject(UUID id, ProjectDto dto, UUID userId) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found"));

		if (!project.getOwnerId().equals(userId)) {
			throw new ResponseStatusException(FORBIDDEN, "Forbidden");
		}

		if (dto.getName() != null)
			project.setName(dto.getName());
		if (dto.getDescription() != null)
			project.setDescription(dto.getDescription());

		return projectRepository.save(project);
	}

	public void deleteProject(UUID id, UUID userId) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found"));

		if (!project.getOwnerId().equals(userId)) {
			throw new ResponseStatusException(FORBIDDEN, "Forbidden");
		}

		projectRepository.delete(project);
	}
}
