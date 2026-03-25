package com.github.renancvitor.inventory.application.category.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.renancvitor.inventory.application.category.dto.CategoryResponse;
import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;

@Service
public class CategoryService {

    public List<CategoryResponse> listCategories() {
        return Arrays.stream(CategoryEnum.values())
                .map(categoryEnum -> new CategoryResponse(
                    categoryEnum.getId(),
                    categoryEnum.name(),
                    categoryEnum.getDisplayName()
                ))
                .toList();
    }

}
