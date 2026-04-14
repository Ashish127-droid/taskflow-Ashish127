package com.taskflow.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.taskflow.dto.TaskDto;
import com.taskflow.entity.Task;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;

	private void assertProjectAccess(UUID projectId, UUID userId) {
		if (projectRepository.findAccessibleProject(projectId, userId).isEmpty()) {
			throw new ResponseStatusException(NOT_FOUND, "Project not found");
		}
	}

	public List<Task> getTasks(UUID projectId, String status, UUID assignee, UUID userId) {
		assertProjectAccess(projectId, userId);
		return taskRepository.findTasks(projectId, status, assignee);
	}

	public Task createTask(UUID projectId, TaskDto dto, UUID userId) {
		assertProjectAccess(projectId, userId);
		Task task = new Task();
		task.setId(UUID.randomUUID());
		task.setTitle(dto.getTitle());
		task.setDescription(dto.getDescription());
		task.setStatus("todo");
		task.setPriority(dto.getPriority() != null ? dto.getPriority() : "medium");
		task.setProjectId(projectId);
		task.setAssigneeId(dto.getAssigneeId());
		task.setDueDate(dto.getDueDate());
		task.setCreatedAt(LocalDateTime.now());

		return taskRepository.save(task);
	}

	public Task updateTask(UUID id, TaskDto dto, UUID userId) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
		assertProjectAccess(task.getProjectId(), userId);

		if (dto.getTitle() != null)
			task.setTitle(dto.getTitle());
		if (dto.getDescription() != null)
			task.setDescription(dto.getDescription());
		if (dto.getStatus() != null)
			task.setStatus(dto.getStatus());
		if (dto.getPriority() != null)
			task.setPriority(dto.getPriority());
		if (dto.getAssigneeId() != null)
			task.setAssigneeId(dto.getAssigneeId());
		if (dto.getDueDate() != null)
			task.setDueDate(dto.getDueDate());

		task.setUpdatedAt(LocalDateTime.now());

		return taskRepository.save(task);
	}

	public void deleteTask(UUID id, UUID userId) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
		assertProjectAccess(task.getProjectId(), userId);
		taskRepository.delete(task);
	}
}
