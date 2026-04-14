package com.taskflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taskflow.entity.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    
    @Query("""
        SELECT DISTINCT p FROM Project p
        LEFT JOIN Task t ON p.id = t.projectId
        WHERE p.ownerId = :userId OR t.assigneeId = :userId
    """)
    List<Project> findUserProjects(UUID userId);

	@Query("""
	    SELECT DISTINCT p FROM Project p
	    LEFT JOIN Task t ON p.id = t.projectId
	    WHERE p.id = :projectId AND (p.ownerId = :userId OR t.assigneeId = :userId)
	    """)
	Optional<Project> findAccessibleProject(UUID projectId, UUID userId);
}