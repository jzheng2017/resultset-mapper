package nl.jiankai.mapper.strategies;

public class LowerCaseDashesFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String transform(String fieldName) {
        String[] splitByCapital = StringUtil.splitStringByCapitalLetter(fieldName);

        return String.join("-", splitByCapital);
    }

    @Override
    public String toString() {
        return "lowercase dashes";
    }
}
