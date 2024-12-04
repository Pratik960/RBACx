package com.rbac.model.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAssignRequest {
    
    @Positive(message = "task id must be more than 1")
    @Schema(example = "1",description = "Enter id of the task")
    private Integer taskId;

    @Positive(message = "user id must be more than 1")
    @Schema(example = "1",description = "Enter id of the user")
    private Integer userId;
}
