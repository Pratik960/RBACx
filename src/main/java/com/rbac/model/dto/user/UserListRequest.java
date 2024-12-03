package com.rbac.model.dto.user;

import com.rbac.util.DefaultPagination;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListRequest {
    @Positive(message = "PerPage will be more than 1")
    @Schema(example = "1",description = "Enter page no to access record")
    private Integer page = DefaultPagination.page;

    @Positive(message = "page will be more than 1")
    @Schema(example = "5",description = "Enter total no of records to be displayed per page")
    private Integer perPage = DefaultPagination.perPage;

    @Schema(example = "john",description = "search user by username")
    private String search;

    @Schema(example = "ASC")
    private String sort;

    @Schema(example = "ACTIVE",description = "status of task")
    private String status;

    @Schema(example = "ROLE_USER", description = "role of user")
    private String role;

    @Schema(example = "firstname", description = "sort the data by firstname,lastname")
    private String sortBy;

    @Schema(example = "2024-01-01", description = "provide start date for filtering records")
    private String startDate;

    @Schema(example = "2024-04-17", description = "provide end date for filtering records")
    private String endDate;

    @Schema(example = "true", description = "want pagination or not")
    private boolean isPageable;
}
