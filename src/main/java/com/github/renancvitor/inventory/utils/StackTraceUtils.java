package com.github.renancvitor.inventory.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StackTraceUtils {

    public static String formatStackTrace(Throwable throwable, int maxLines) {
        return Arrays.stream(throwable.getStackTrace())
                .limit(maxLines)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
