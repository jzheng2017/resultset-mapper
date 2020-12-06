package nl.jiankai.mapper.strategies;

import java.util.Arrays;

class StringUtil {

    static String[] splitStringByCapitalLetter(String s) {
        String[] splitByCapital = s.split("(?=\\p{Lu})");
        return Arrays.stream(splitByCapital)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }
}
