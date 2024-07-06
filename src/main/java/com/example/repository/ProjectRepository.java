package com.example.repository;

import com.example.entity.Project;
import com.example.entity.User;
import com.example.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    Project findByProjectCode(String code);
    List<Project> findAllByAssignedManager(User manager);
    List<Project> findAllByProjectStatusIsNotAndAssignedManager(Status status, User assignedManager);

}
