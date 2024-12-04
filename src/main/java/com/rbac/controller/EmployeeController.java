package com.rbac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbac.model.dto.task.TaskAssignRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.service.TaskService;
import com.rbac.util.http.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Employee", description = "Employee API")
@RestController
@Slf4j
@RequestMapping("/api/emp")
@CrossOrigin(origins = "http://localhost:5001")
@PreAuthorize("hasAuthority('ROLE_EMP')")
public class EmployeeController {

    private final TaskService taskService;

    @Autowired
    public EmployeeController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Assign task to user", description = "assigns task to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully assigned to user"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User or task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PutMapping()
    public ResponseEntity<SuccessResponse<String>> assignTask(
            @Valid @RequestBody TaskAssignRequest assignRequest) {
        SuccessResponse<String> response = taskService.assignTask(assignRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
