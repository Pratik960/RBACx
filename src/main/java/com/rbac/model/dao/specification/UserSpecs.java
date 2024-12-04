package com.rbac.model.dao.specification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rbac.config.security.web.UserAuthorities;
import com.rbac.model.dto.user.UserListRequest;
import com.rbac.model.entity.Users;
import com.rbac.util.AppUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecs {
    public static Specification<Users> getUserList(UserListRequest userListRequest) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = applyUserFilter(criteriaBuilder, root, userListRequest);
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private static List<Predicate> applyUserFilter(CriteriaBuilder criteriaBuilder, Root<?> root,
            UserListRequest userListRequest) {
        List<Predicate> predicates = new ArrayList<>();
        String search = userListRequest.getSearch();
        String status = userListRequest.getStatus();
        String role = userListRequest.getRole();
        // search by username
        if (!AppUtil.isNullOrEmptyString(search)) {
            predicates.add(criteriaBuilder.like(root.get("username"), "%" + search + "%"));
        }

        // filter by status
        if (!AppUtil.isNullOrEmptyString(status)) {
            predicates.add(criteriaBuilder.equal(root.get("status"), Users.UserStatus.valueOf(status)));
        }

        // filter by role
        if(!AppUtil.isNullOrEmptyString(role)){
            predicates.add(criteriaBuilder.equal(root.get("authorities"), UserAuthorities.valueOf(role)));
        }

        // filter by date
        Date startDate = AppUtil.parseDate(userListRequest.getStartDate());
        Date endDate = AppUtil.parseDate(userListRequest.getEndDate());
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
