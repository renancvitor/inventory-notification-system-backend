package com.github.renancvitor.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.dto.ProductListingData;
import com.github.renancvitor.inventory.service.ProductService;

@RestController
@RequestMapping("/produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductListingData>> list(@RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = ("name")) Pageable pageable) {

        Page<ProductListingData> page = productService.list(pageable, active);
        return ResponseEntity.ok(page);
    }

}
