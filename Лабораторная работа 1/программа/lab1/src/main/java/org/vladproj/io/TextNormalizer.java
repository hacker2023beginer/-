package org.vladproj.io;

import java.util.ArrayList;
import java.util.List;

public class TextNormalizer {

    public static List<Integer> getSpacePositions(String text) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') positions.add(i);
        }
        return positions;
    }

    public static String removeSpaces(String text) {
        return text.replace(" ", "");
    }

    public static String restoreSpaces(String text, List<Integer> positions) {
        StringBuilder sb = new StringBuilder(text);
        for (int pos : positions) {
            if (pos <= sb.length()) sb.insert(pos, ' ');
        }
        return sb.toString();
    }
}
