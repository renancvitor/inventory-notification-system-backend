package com.github.renancvitor.inventory.application.order.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.application.order.dto.OrderCreationData;
import com.github.renancvitor.inventory.application.order.dto.OrderDetailData;
import com.github.renancvitor.inventory.application.order.dto.OrderUpdateData;
import com.github.renancvitor.inventory.application.order.service.OrderService;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.util.PageMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CustomPage<OrderDetailData>> list(
            @PageableDefault(size = 10, sort = ("id")) Pageable pageable,
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam(required = false) Integer orderStatusId,
            @RequestParam(required = false) Long requestedBy,
            @RequestParam(required = false) Long approvedBy,
            @RequestParam(required = false) Long rejectedBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAt,
            @RequestParam(required = false) BigDecimal totalValue) {

        var page = orderService.list(pageable, loggedInUser, orderStatusId, requestedBy, approvedBy, rejectedBy,
                createdAt, updatedAt, totalValue);
        return ResponseEntity.ok(PageMapper.toCustomPage(page));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<OrderDetailData> create(@RequestBody @Valid OrderCreationData data,
            UriComponentsBuilder uriComponentsBuilder,
            @AuthenticationPrincipal User loggedInUser) {
        OrderDetailData orderDetailData = orderService.create(data, loggedInUser);

        URI uri = uriComponentsBuilder.path("/orders/{id}")
                .buildAndExpand(orderDetailData.id())
                .toUri();

        return ResponseEntity.created(uri).body(orderDetailData);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailData> update(@PathVariable Long id,
            @RequestBody @Valid OrderUpdateData data,
            @AuthenticationPrincipal User loggedInUser) {
        OrderDetailData orderDetailData = orderService.update(id, data, loggedInUser);

        return ResponseEntity.ok(orderDetailData);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<OrderDetailData> reject(@PathVariable Long id,
            @AuthenticationPrincipal User loggedInUser) {
        OrderDetailData orderDetailData = orderService.reject(id, loggedInUser);

        return ResponseEntity.ok(orderDetailData);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<OrderDetailData> approve(@PathVariable Long id,
            @AuthenticationPrincipal User loggedInUser) {
        OrderDetailData orderDetailData = orderService.approve(id, loggedInUser);

        return ResponseEntity.ok(orderDetailData);
    }

}
