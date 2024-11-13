package com.brekfst.sakuratags.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static Method COLOR_FROM_CHAT_COLOR;
    private static Method CHAT_COLOR_FROM_COLOR;
    private static final boolean hexSupport;
    private static final Pattern gradientPattern = Pattern.compile("<(#[A-Fa-f0-9]{6})>(.*?)</(#[A-Fa-f0-9]{6})>");
    private static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern legacyPattern = Pattern.compile("&[0-9a-fk-or]");

    static {
        try {
            COLOR_FROM_CHAT_COLOR = ChatColor.class.getDeclaredMethod("getColor");
            CHAT_COLOR_FROM_COLOR = ChatColor.class.getDeclaredMethod("of", Color.class);
        } catch (NoSuchMethodException e) {
            COLOR_FROM_CHAT_COLOR = null;
            CHAT_COLOR_FROM_COLOR = null;
        }
        hexSupport = CHAT_COLOR_FROM_COLOR != null;
    }

    public static String parseColors(String text) {
        return parseColors(text, '&');
    }

    public static String parseColors(String text, char colorSymbol) {
        if (text == null) return "";

        // Process gradients
        Matcher gradientMatcher = gradientPattern.matcher(text);
        while (gradientMatcher.find()) {
            Color start = Color.decode(gradientMatcher.group(1));
            String innerText = gradientMatcher.group(2);
            Color end = Color.decode(gradientMatcher.group(3));
            String gradient = applyGradient(innerText, start, end, colorSymbol);
            text = text.replace(gradientMatcher.group(0), gradient);
        }

        // Process hex colors
        Matcher hexMatcher = hexPattern.matcher(text);
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            if (hexSupport) {
                text = text.replace(hexMatcher.group(0), ChatColor.of("#" + hexColor) + "");
            }
        }

        // Process legacy codes
        text = ChatColor.translateAlternateColorCodes(colorSymbol, text);
        return text;
    }

    public static String stripColors(String text) {
        return ChatColor.stripColor(text);
    }

    public static List<Character> getCharactersWithoutColors(String text) {
        text = stripColors(text);
        List<Character> result = new ArrayList<>();
        for (char character : text.toCharArray()) {
            result.add(character);
        }
        return result;
    }

    public static List<String> getCharactersWithColors(String text, char colorSymbol) {
        List<String> result = new ArrayList<>();
        StringBuilder colorCode = new StringBuilder();
        boolean isColorCode = false;

        for (char character : text.toCharArray()) {
            if (character == colorSymbol) {
                isColorCode = true;
                colorCode = new StringBuilder().append(colorSymbol);
            } else if (isColorCode) {
                colorCode.append(character);
                result.add(colorCode.toString());
                isColorCode = false;
            } else {
                result.add(colorCode + String.valueOf(character));
            }
        }
        return result;
    }

    private static String applyGradient(String text, Color start, Color end, char colorSymbol) {
        StringBuilder gradientText = new StringBuilder();
        List<String> characters = getCharactersWithColors(text, colorSymbol);
        double[] redSteps = calculateGradientSteps(start.getRed(), end.getRed(), characters.size());
        double[] greenSteps = calculateGradientSteps(start.getGreen(), end.getGreen(), characters.size());
        double[] blueSteps = calculateGradientSteps(start.getBlue(), end.getBlue(), characters.size());

        for (int i = 0; i < characters.size(); i++) {
            Color color = new Color((int) redSteps[i], (int) greenSteps[i], (int) blueSteps[i]);
            gradientText.append(ChatColor.of(color)).append(characters.get(i));
        }
        return gradientText.toString();
    }

    private static double[] calculateGradientSteps(int start, int end, int steps) {
        double[] result = new double[steps];
        double delta = (double) (end - start) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            result[i] = start + (delta * i);
        }
        return result;
    }

    private static ChatColor fromColor(Color color) {
        try {
            return (ChatColor) CHAT_COLOR_FROM_COLOR.invoke(null, color);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to convert Color to ChatColor", e);
        }
    }
}