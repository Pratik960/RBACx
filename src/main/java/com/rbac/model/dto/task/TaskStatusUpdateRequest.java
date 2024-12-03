package com.rbac.model.dto.task;

import com.rbac.model.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequest {
    
    private Integer taskId;
    private TaskStatus status;
}
