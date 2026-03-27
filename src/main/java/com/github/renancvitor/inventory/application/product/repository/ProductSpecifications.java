package com.github.renancvitor.inventory.application.product.repository;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import com.github.renancvitor.inventory.domain.entity.category.enums.CategoryEnum;
import com.github.renancvitor.inventory.domain.entity.product.Product;

public class ProductSpecifications {

    public static Specification<Product> search(String search) {
        String normalizedSearch = search.toLowerCase(Locale.ROOT);
        List<String> matchingCategoryNames = findMatchingCategoryNames(search);

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.like(builder.lower(root.get("productName")), "%" + normalizedSearch + "%"));
            predicates.add(builder.like(builder.lower(root.get("brand")), "%" + normalizedSearch + "%"));
            predicates.add(builder.like(builder.lower(root.get("category").get("categoryName")), "%" + normalizedSearch + "%"));

            if (!matchingCategoryNames.isEmpty()) {
                predicates.add(root.get("category").get("categoryName").in(matchingCategoryNames));
            }

            return builder.or(predicates.toArray(Predicate[]::new));
        };
    }

    static List<String> findMatchingCategoryNames(String search) {
        String normalizedSearch = normalize(search);

        return java.util.Arrays.stream(CategoryEnum.values())
                .filter(category -> normalize(category.getDisplayName()).contains(normalizedSearch))
                .map(Enum::name)
                .toList();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }

    public static Specification<Product> active(Boolean active) {
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }

    public static Specification<Product> categoryId(Integer categoryId) {
        return (root, query, builder) -> builder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

}
