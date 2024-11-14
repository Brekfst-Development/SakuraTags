package com.brekfst.sakuratags.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern gradientPattern = Pattern.compile("<gradient:([^>]+):([^>]+)>(.*?)</gradient>", Pattern.DOTALL);
    private static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String parseColors(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Process gradients
        text = processGradients(text);

        // Process hex colors
        text = processHexColors(text);

        // Process legacy color codes directly
        text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    private static String processGradients(String text) {
        Matcher gradientMatcher = gradientPattern.matcher(text);
        while (gradientMatcher.find()) {
            Color start = Color.decode(gradientMatcher.group(1));
            Color end = Color.decode(gradientMatcher.group(2));
            String innerText = gradientMatcher.group(3);
            String gradient = applyGradient(innerText, start, end);
            text = text.replace(gradientMatcher.group(0), gradient);
        }
        return text;
    }

    private static String processHexColors(String text) {
        Matcher hexMatcher = hexPattern.matcher(text);
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            String replacement = ChatColor.of("#" + hexColor).toString();
            text = text.replace(hexMatcher.group(0), replacement);
        }
        return text;
    }

    private static String applyGradient(String text, Color start, Color end) {
        StringBuilder gradientText = new StringBuilder();
        int length = text.length();
        double[] redSteps = calculateGradientSteps(start.getRed(), end.getRed(), length);
        double[] greenSteps = calculateGradientSteps(start.getGreen(), end.getGreen(), length);
        double[] blueSteps = calculateGradientSteps(start.getBlue(), end.getBlue(), length);

        for (int i = 0; i < length; i++) {
            Color color = new Color((int) redSteps[i], (int) greenSteps[i], (int) blueSteps[i]);
            String coloredChar = ChatColor.of(color).toString() + text.charAt(i);
            gradientText.append(coloredChar);
        }
        gradientText.append(ChatColor.RESET); // Reset formatting at the end
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
}
