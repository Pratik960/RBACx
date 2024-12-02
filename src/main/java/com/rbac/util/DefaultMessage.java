package com.rbac.util;

public enum DefaultMessage {
    ALREADY_PRESENT("%s can not be processed. It is already present"),
    CREATE_SUCCESS("%s created successfully"),
    CREATE_ERROR("There is something wrong while creating %s."),
    SAVE_SUCCESS("%s saved successfully"),
    SAVE_ERROR("There is something wrong while saving %s"),
    CONNECT_ERROR("There is something wrong while connecting %s"),
    CONNECT_SUCCESS("%s connected successfully."),
    UPDATE_SUCCESS("%s updated successfully"),
    UPDATE_ERROR("There is something wrong while updating %s"),
    DELETE_SUCCESS("%s deleted successfully"),
    DELETE_ERROR("There is something wrong while deleting %s"),
    FETCH_ERROR("There is something wrong while fetching %s data."),
    RESOURCE_NOT_FOUND("%s not found."),
    RESOURCE_NOT_FOUND_BY("%s not found by %s."),
    UNAUTHORIZE_REQUEST("Unauthorized request."),
    UNAUTHORIZE_USER("Unauthorized user."),
    INTERNAL_SERVER_ERROR("There is something wrong while %s."),
    SUCCESS("Operation Successful"),
    UPSERT_ERROR("An error occurred while processing %s"),
    CUSTOM_EXCEPTION("Custom exception occurred while %s.");

    private final String message;

    DefaultMessage(String message) {
        this.message = message;
    }

    public String getMessage(String... obj) {
        return String.format(message, (Object[]) obj);
    }

}
