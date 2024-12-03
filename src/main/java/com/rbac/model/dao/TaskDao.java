package com.rbac.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rbac.model.entity.Task;

public interface TaskDao extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task>{
    
}
