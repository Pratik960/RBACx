package com.rbac.model.dto.task;

import com.rbac.model.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    
    private Integer taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer assignedUser;
    private String assignedUserName;

}
