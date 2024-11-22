package com.khi.scvotingapp.util;

public class AppUtil {

    private static String date;

    public AppUtil(String date) {
        AppUtil.date = date;
    }

    public static String toTitleCase(String text) {
        text = text.toLowerCase().trim().replaceAll("\\s+", " ");
        StringBuilder titleCase = new StringBuilder(text.length());
        boolean nextTitleCase = true;

        for (char c : text.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static String getDate() {
        return date;
    }
}
