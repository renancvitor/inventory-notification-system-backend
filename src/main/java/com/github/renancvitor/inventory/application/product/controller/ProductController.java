package com.github.renancvitor.inventory.application.product.controller;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.application.movement.dto.MovementWithOrderRequest;
import com.github.renancvitor.inventory.application.product.dto.InputProductResponse;
import com.github.renancvitor.inventory.application.product.dto.OutputProductResponse;
import com.github.renancvitor.inventory.application.product.dto.ProductCreationData;
import com.github.renancvitor.inventory.application.product.dto.ProductDetailData;
import com.github.renancvitor.inventory.application.product.dto.ProductListingData;
import com.github.renancvitor.inventory.application.product.dto.ProductUpdateData;
import com.github.renancvitor.inventory.application.product.service.ProductService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.util.PageMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CustomPage<ProductListingData>> list(@RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = ("productName")) Pageable pageable,
            @AuthenticationPrincipal User loggedInUser) {

        var page = productService.list(pageable, active, categoryId, minPrice, maxPrice,
                loggedInUser);
        return ResponseEntity.ok(PageMapper.toCustomPage(page));
    }

    @PostMapping
    public ResponseEntity<ProductDetailData> create(@RequestBody @Valid ProductCreationData data,
            UriComponentsBuilder uriComponentsBuilder,
            @AuthenticationPrincipal User loggedInUser) {
        ProductDetailData productDetailData = productService.create(data, loggedInUser);

        URI uri = uriComponentsBuilder.path("/products/{id}")
                .buildAndExpand(productDetailData.id())
                .toUri();

        return ResponseEntity.created(uri).body(productDetailData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailData> update(@PathVariable Long id, @RequestBody @Valid ProductUpdateData data,
            @AuthenticationPrincipal User loggedInUser) {
        ProductDetailData productDetailData = productService.update(id, data, loggedInUser);

        return ResponseEntity.ok(productDetailData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        productService.delete(id, loggedInUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        productService.activate(id, loggedInUser);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/return-to-stock")
    public ResponseEntity<InputProductResponse> returnToStock(
            @PathVariable Long id,
            @Valid @RequestBody MovementWithOrderRequest request,
            @AuthenticationPrincipal User loggedInUser) {

        InputProductResponse response = productService.inputProduct(id, request, loggedInUser);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/return-to-supplier")
    public ResponseEntity<OutputProductResponse> returnToSupplier(
            @PathVariable Long id,
            @Valid @RequestBody MovementWithOrderRequest request,
            @AuthenticationPrincipal User loggedInUser) {

        OutputProductResponse response = productService.outputProduct(id, request, loggedInUser);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/purchase")
    public ResponseEntity<InputProductResponse> purchase(
            @PathVariable Long id,
            @Valid @RequestBody MovementWithOrderRequest request,
            @AuthenticationPrincipal User loggedInUser) {

        InputProductResponse response = productService.inputProduct(id, request, loggedInUser);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/consume")
    public ResponseEntity<OutputProductResponse> consume(
            @PathVariable Long id,
            @Valid @RequestBody MovementWithOrderRequest request,
            @AuthenticationPrincipal User loggedInUser) {

        OutputProductResponse response = productService.outputProduct(id, request, loggedInUser);
        return ResponseEntity.ok(response);
    }

}
