package mapper.strategies;

import nl.jiankai.mapper.strategies.LowerCaseUnderscoreFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LowerCaseUnderscoreFieldNamingStrategyTest {
    private final String toBeTransformedString = "unitTestingFieldNamingStrategyTest";
    private LowerCaseUnderscoreFieldNamingStrategy sut;

    @BeforeEach
    void setup() {
        sut = new LowerCaseUnderscoreFieldNamingStrategy();
    }

    @Test
    void lowerCaseUnderscoreFieldNamingStrategyTransformsStringToLowerCaseUnderscore() {
        final String expectedString = "unit_testing_field_naming_strategy_test";
        final String actualString = sut.transform(toBeTransformedString);

        Assertions.assertEquals(expectedString, actualString);
    }
}
