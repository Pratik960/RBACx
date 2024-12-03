package com.rbac.service;

import org.springframework.data.domain.Page;

import com.rbac.model.dto.task.TaskAssignRequest;
import com.rbac.model.dto.task.TaskListRequest;
import com.rbac.model.dto.task.TaskRequest;
import com.rbac.model.dto.task.TaskResponse;
import com.rbac.model.dto.task.TaskStatusUpdateRequest;
import com.rbac.model.entity.Task;
import com.rbac.model.entity.Users;
import com.rbac.util.http.response.PageResponse;
import com.rbac.util.http.response.SuccessResponse;

public interface TaskService {

    SuccessResponse<TaskResponse> addTask(TaskRequest taskRequest);

    SuccessResponse<String> assignTask(TaskAssignRequest assignRequest);

    SuccessResponse<TaskResponse> updateTaskStatus(TaskStatusUpdateRequest updateRequest);

    PageResponse<TaskResponse> getAllTasks(TaskListRequest listRequest);

    Page<Task> getAllTasksByRequest (TaskListRequest taskListRequest, boolean isPageable, Users users);
    
}
