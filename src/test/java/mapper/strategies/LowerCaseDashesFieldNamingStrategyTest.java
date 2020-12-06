package mapper.strategies;

import nl.jiankai.mapper.strategies.LowerCaseDashesFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LowerCaseDashesFieldNamingStrategyTest {
    private LowerCaseDashesFieldNamingStrategy sut;
    private final String toBeTransformedString = "unitTestingFieldNamingStrategyTest";

    @BeforeEach
    void setup() {
        sut = new LowerCaseDashesFieldNamingStrategy();
    }

    @Test
    void lowerCaseUnderscoreFieldNamingStrategyTransformsStringToLowerCaseUnderscore() {
        final String expectedString = "unit-testing-field-naming-strategy-test";
        final String actualString = sut.transform(toBeTransformedString);

        Assertions.assertEquals(expectedString, actualString);
    }
}
