package com.rbac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.rbac.config.security.web.UserAuthorities;
import com.rbac.model.dao.TaskDao;
import com.rbac.model.dao.UsersDao;
import com.rbac.model.dao.specification.TaskSpecs;
import com.rbac.model.dto.task.TaskAssignRequest;
import com.rbac.model.dto.task.TaskListRequest;
import com.rbac.model.dto.task.TaskRequest;
import com.rbac.model.dto.task.TaskResponse;
import com.rbac.model.dto.task.TaskStatusUpdateRequest;
import com.rbac.model.entity.Task;
import com.rbac.model.entity.Task.TaskStatus;
import com.rbac.model.entity.Users;
import com.rbac.service.TaskService;
import com.rbac.util.DefaultMessage;
import com.rbac.util.ObjectUtil;
import com.rbac.util.http.exceptions.CustomException;
import com.rbac.util.http.exceptions.InternalServerErrorException;
import com.rbac.util.http.exceptions.ResourceNotFoundException;
import com.rbac.util.http.response.PageResponse;
import com.rbac.util.http.response.SuccessResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;
    private final UsersDao usersDao;

    @Autowired
    public TaskServiceImpl(TaskDao taskDao, UsersDao usersDao) {
        this.taskDao = taskDao;
        this.usersDao = usersDao;
    }

    @Override
    public SuccessResponse<TaskResponse> addTask(TaskRequest taskRequest) {
        try {

            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(Task.TaskStatus.CREATED);

            task = taskDao.save(task);

            TaskResponse response = parseTaskToTaskResponse(task);

            return new SuccessResponse<>(response, HttpStatus.CREATED.value());

        } catch (CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex.getMessage(), ex);
        }
    }

    @Override
    public SuccessResponse<TaskResponse> updateTaskStatus(TaskStatusUpdateRequest updateRequest) {
        try {
            Task task = taskDao.findById(updateRequest.getTaskId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("Task")));

            if (task.getStatus() == TaskStatus.DELETED || task.getStatus() == TaskStatus.COMPLETED) {
                throw new CustomException("You cannot update this task because it is already deleted or completed.");
            }
            task.setStatus(updateRequest.getStatus());
            task = taskDao.save(task);
            TaskResponse response = parseTaskToTaskResponse(task);
            return new SuccessResponse<>(response, HttpStatus.OK.value());
        } catch (CustomException | ResourceNotFoundException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("updating status of task"));
        }
    }

    @Override
    public PageResponse<TaskResponse> getAllTasks(TaskListRequest listRequest) {
        try {
            Users user = null;
            if(listRequest.getUserId() != 0){
                user = usersDao.findById(listRequest.getUserId())
                    .orElseThrow(() -> new CustomException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));
            }
            Page<Task> rsTasks = getAllTasksByRequest(listRequest, listRequest.isPageable(), user);

            List<TaskResponse> taskResponses = rsTasks.stream()
                    .map(this::parseTaskToTaskResponse)
                    .toList();

            PageResponse<TaskResponse> response = new PageResponse<>();
            response.setLimit(rsTasks.getSize());
            response.setPage(rsTasks.getNumber() + 1);
            response.setTotal(rsTasks.getTotalElements());
            response.setTotalPage(rsTasks.getTotalPages());
            response.setData(taskResponses);

            return response;

        } catch (Exception e) {
            log.error(DefaultMessage.INTERNAL_SERVER_ERROR.getMessage(e.getMessage()), e);
            throw new CustomException(DefaultMessage.FETCH_ERROR.getMessage("all tasks"));
        }
    }

    @Override
    public Page<Task> getAllTasksByRequest(TaskListRequest taskListRequest, boolean isPageable, Users users) {
        Pageable pageable;
        if (isPageable) {
            pageable = ObjectUtil.getPageable(taskListRequest.getPage(), taskListRequest.getPerPage(),
                    taskListRequest.getSortBy(), taskListRequest.getSort());
        } else {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        }
        Specification<Task> rsSpecification = TaskSpecs.getTaskList(taskListRequest, users);
        return taskDao.findAll(rsSpecification, pageable);
    }

    
    @Override
    public SuccessResponse<String> assignTask(TaskAssignRequest assignRequest) {
        try{

            Task task = taskDao.findById(assignRequest.getTaskId())
            .orElseThrow(() -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("Task")));

            if(task.getStatus() != Task.TaskStatus.CREATED){
                throw new CustomException("Task can't be assigned");
            }

            Users user = usersDao.findById(assignRequest.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            if(user.getAuthorities() != UserAuthorities.ROLE_USER){
                throw new CustomException("You can only assign task to user");
            }

            task.setAssignedUser(user);
            task.setStatus(Task.TaskStatus.ASSIGNED);
            taskDao.save(task);

            return new SuccessResponse<>(String.format("Task assigned to %s successfully.", user.getUsername()), HttpStatus.OK.value());
        }catch (ResourceNotFoundException | CustomException ex){
            throw new CustomException(ex.getMessage(), ex);
        }catch( Exception ex){
            throw new InternalServerErrorException(DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("assigning task"));
        }
    }

    private TaskResponse parseTaskToTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();

        taskResponse.setTaskId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setAssignedUser(task.getAssignedUser() != null ? task.getAssignedUser().getId() : 0);
        taskResponse.setAssignedUserName(task.getAssignedUser() != null ? task.getAssignedUser().getUsername() : "");
        return taskResponse;

    }

}
