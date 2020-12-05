package nl.jiankai.mapper.strategies;

public class IdentityFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String transform(String fieldName) {
        return fieldName;
    }

    @Override
    public String toString() {
        return "identity";
    }
}
