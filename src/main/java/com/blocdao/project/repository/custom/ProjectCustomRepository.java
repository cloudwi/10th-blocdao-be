package com.blocdao.project.repository.custom;

import com.blocdao.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectCustomRepository {
    Page<Project> findAllProjects(Pageable pageable);
}
