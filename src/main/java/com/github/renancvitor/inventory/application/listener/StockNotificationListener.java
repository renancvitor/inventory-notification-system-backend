package com.github.renancvitor.inventory.application.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.renancvitor.inventory.application.product.repository.ProductRepository;
import com.github.renancvitor.inventory.application.product.service.StockMonitorService;
import com.github.renancvitor.inventory.application.user.repository.UserRepository;
import com.github.renancvitor.inventory.domain.entity.product.Product;
import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.domain.events.StockBelowMinimumEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockNotificationListener {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockMonitorService stockMonitorService;

    @EventListener
    public void handler(StockBelowMinimumEvent event) {
        Product product = productRepository.findById(event.productId())
                .orElseThrow();
        User loggedUser = userRepository.findById(event.userId())
                .orElseThrow();

        stockMonitorService.handleLowStock(product, loggedUser);
    }

}
