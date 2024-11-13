package com.brekfst.sakuratags.utils;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Pattern;

public class ColorFormatter {

    public static String parse(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String prefix(String text) {
        return parse("&7[&dSakuraTags&7] &r" + text);
    }

    public static String stripColor(String text) {
        return ChatColor.stripColor(text);
    }
}
