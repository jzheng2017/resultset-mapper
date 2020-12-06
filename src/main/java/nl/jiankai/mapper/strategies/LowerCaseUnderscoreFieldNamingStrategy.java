package nl.jiankai.mapper.strategies;

public class LowerCaseUnderscoreFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String transform(String fieldName) {
        String[] splitByCapital = StringUtil.splitStringByCapitalLetter(fieldName);

        return String.join("_", splitByCapital);
    }

    @Override
    public String toString() {
        return "lowercase underscore";
    }
}
