package com.rbac.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
public class ObjectUtil {
    public static Pageable getPageable(int page, int perPage, String sortBy, String sort) {
        Pageable pageable = null;
        page = page - 1;
        sortBy = (sortBy == null || sortBy.isEmpty()) ? "id" : sortBy;
        sort = (sort == null || sort.isEmpty()) ? "desc" : sort;
        try {
            if (!AppUtil.isNullOrEmptyString(sort)) {
                if (sort.equalsIgnoreCase("desc")) {
                    pageable = PageRequest.of(page, perPage, Sort.by(sortBy).descending());
                } else {
                    pageable = PageRequest.of(page, perPage, Sort.by(sortBy));
                }
            } else {
                pageable = PageRequest.of(page, perPage, Sort.by(sortBy).descending());
            }

            return pageable;
        } catch (Exception ex) {
            log.error("Exception occurred while getting pageable: " + ex.getMessage(), ex);
            return pageable;
        }

    }
}
