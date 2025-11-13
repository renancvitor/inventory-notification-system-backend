package com.github.renancvitor.inventory.util;

import java.util.List;

public record CustomPage<T>(
                List<T> content,
                int page,
                int size,
                long totalElements,
                int totalPages,
                boolean last) {

        public List<T> getContent() {
                return content;
        }

}
