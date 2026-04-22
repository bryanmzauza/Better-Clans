package com.bryanmz.betterclans.util;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Valida tags de cla: exatamente 3 caracteres (A-Z ou 0-9) e nao reservadas.
 */
public final class TagValidator {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z0-9]{3}$");

    private final Set<String> reserved;

    public TagValidator(Set<String> reserved) {
        this.reserved = Set.copyOf(reserved);
    }

    public Result validate(String rawTag) {
        if (rawTag == null) return Result.INVALID_FORMAT;
        String tag = rawTag.toUpperCase();
        if (!PATTERN.matcher(tag).matches()) return Result.INVALID_FORMAT;
        if (reserved.contains(tag)) return Result.RESERVED;
        return Result.OK;
    }

    public static String normalize(String rawTag) {
        return rawTag == null ? null : rawTag.toUpperCase();
    }

    public enum Result {
        OK,
        INVALID_FORMAT,
        RESERVED
    }
}
