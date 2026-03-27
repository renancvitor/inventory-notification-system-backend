package com.github.renancvitor.inventory.application.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ProductSpecificationsTests {

    @Test
    void shouldMatchCategoryNameFromDisplayName() {
        List<String> categories = ProductSpecifications.findMatchingCategoryNames("Bebida");

        assertEquals(List.of("DRINK"), categories);
    }

    @Test
    void shouldMatchCategoryNameFromDisplayNameWithoutAccent() {
        List<String> categories = ProductSpecifications.findMatchingCategoryNames("Eletronico");

        assertEquals(List.of("ELECTRONICS"), categories);
    }

    @Test
    void shouldReturnEmptyListWhenSearchDoesNotMatchAnyCategoryDisplayName() {
        List<String> categories = ProductSpecifications.findMatchingCategoryNames("Marca XPTO");

        assertTrue(categories.isEmpty());
    }
}
