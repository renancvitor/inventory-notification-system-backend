package com.github.renancvitor.inventory.util;

import org.springframework.data.domain.Page;

public class PageMapper {

    public static <T> CustomPage<T> toCustomPage(Page<T> page) {
        return new CustomPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

}
