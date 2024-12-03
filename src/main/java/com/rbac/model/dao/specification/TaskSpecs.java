package com.rbac.model.dao.specification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rbac.model.dto.task.TaskListRequest;
import com.rbac.model.entity.Task;
import com.rbac.model.entity.Users;
import com.rbac.util.AppUtil;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSpecs {
    public static Specification<Task> getTaskList(TaskListRequest taskListRequest, Users users) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = applyTaskFilter(criteriaBuilder, root, taskListRequest, users);
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private static List<Predicate> applyTaskFilter(CriteriaBuilder criteriaBuilder, Root<?> root,
            TaskListRequest taskListRequest, Users users) {
        List<Predicate> predicates = new ArrayList<>();
        String search = taskListRequest.getSearch();
        String status = taskListRequest.getStatus();

        // search by title
        if (!AppUtil.isNullOrEmptyString(search)) {
            predicates.add(criteriaBuilder.like(root.get("title"), "%" + search + "%"));
        }

        if (!AppUtil.isNullOrEmptyString(users)) {
            predicates.add(criteriaBuilder.equal(root.get("assignedUser"), users));
        }

        if (!AppUtil.isNullOrEmptyString(status)) {
            predicates.add(criteriaBuilder.equal(root.get("status"), Task.TaskStatus.valueOf(status)));
        }

        Date startDate = AppUtil.parseDate(taskListRequest.getStartDate());
        Date endDate = AppUtil.parseDate(taskListRequest.getEndDate());
        if (!AppUtil.isNullOrEmptyString(endDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            endDate = calendar.getTime();
        }
        if (!AppUtil.isNullOrEmptyString(startDate) && !AppUtil.isNullOrEmptyString(endDate)) {
            predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
        } else if (!AppUtil.isNullOrEmptyString(startDate)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        } else if (!AppUtil.isNullOrEmptyString(endDate)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }

        return predicates;
    }
}
