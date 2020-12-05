package nl.jiankai.mapper.strategies;

import java.util.Arrays;

public class LowerUnderscoreFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String transform(String fieldName) {
        String[] splitByCapital = fieldName.split("(?=\\p{Lu})");
        splitByCapital = Arrays.stream(splitByCapital)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        return String.join("_", splitByCapital);
    }

    @Override
    public String toString() {
        return "lowercase underscore";
    }
}
