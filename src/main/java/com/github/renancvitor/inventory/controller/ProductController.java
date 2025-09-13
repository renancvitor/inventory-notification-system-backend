package com.github.renancvitor.inventory.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.dto.product.ProductCreationData;
import com.github.renancvitor.inventory.dto.product.ProductDetailData;
import com.github.renancvitor.inventory.dto.product.ProductListingData;
import com.github.renancvitor.inventory.service.ProductService;

import jakarta.validation.Valid;

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

    @PostMapping
    public ResponseEntity<ProductDetailData> create(@RequestBody @Valid ProductCreationData data,
            UriComponentsBuilder uriComponentsBuilder) {
        ProductDetailData productDetailData = productService.create(data);

        URI uri = uriComponentsBuilder.path("/produtos/{id}")
                .buildAndExpand(productDetailData.id())
                .toUri();

        return ResponseEntity.created(uri).body(productDetailData);
    }

}
