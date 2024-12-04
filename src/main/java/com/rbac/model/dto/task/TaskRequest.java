package com.rbac.model.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
    @NotBlank(message = "Task title must not be blank.")
    @Schema(example = "testTitle",description = "Title of the task")
    private String title;

    @NotBlank(message = "Task description must not be blank.")
    @Schema(example = "exampleDescription",description = "Description of the task")
    private String description;
}
