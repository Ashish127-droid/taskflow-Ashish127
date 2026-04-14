package com.taskflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.taskflow.entity.Task;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

	@Query("""
			    SELECT t FROM Task t
			    WHERE t.projectId = :projectId
			    AND (:status IS NULL OR t.status = :status)
			    AND (:assigneeId IS NULL OR t.assigneeId = :assigneeId)
			""")
	List<Task> findTasks(UUID projectId, String status, UUID assigneeId);
}
