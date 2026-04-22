package com.bryanmz.betterclans.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Locale;

public final class ColorUtil {

    private ColorUtil() {}

    /**
     * Aceita nomes (ex: "red") ou hex ("#ff0000"). Retorna branco em caso de falha.
     */
    public static TextColor parse(String input) {
        if (input == null || input.isBlank()) return NamedTextColor.WHITE;
        String value = input.trim().toLowerCase(Locale.ROOT);
        if (value.startsWith("#")) {
            TextColor hex = TextColor.fromHexString(value);
            return hex != null ? hex : NamedTextColor.WHITE;
        }
        NamedTextColor named = NamedTextColor.NAMES.value(value);
        return named != null ? named : NamedTextColor.WHITE;
    }

    public static boolean isValid(String input) {
        if (input == null || input.isBlank()) return false;
        String value = input.trim().toLowerCase(Locale.ROOT);
        if (value.startsWith("#")) {
            return TextColor.fromHexString(value) != null;
        }
        return NamedTextColor.NAMES.value(value) != null;
    }
}
