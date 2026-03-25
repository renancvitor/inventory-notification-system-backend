package com.github.renancvitor.inventory.application.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.application.category.dto.CategoryResponse;
import com.github.renancvitor.inventory.application.category.service.CategoryService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> listCategories() {
        return categoryService.listCategories();
    }
    
}
