package com.rbac.model.dto.task;

import com.rbac.model.entity.Task.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateRequest {

    @Positive(message = "Task id must be more than 1")
    @Schema(example = "1", description = "Id of the task")
    private Integer taskId;


    @Schema(example = "CREATED",description = "Status of the task(i.e. CREATED, ASSIGNED, COMPLETED, DELETED)")
    private TaskStatus status;
}
