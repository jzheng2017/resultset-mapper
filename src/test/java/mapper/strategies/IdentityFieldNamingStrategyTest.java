package mapper.strategies;

import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IdentityFieldNamingStrategyTest {
    private IdentityFieldNamingStrategy sut;
    private final String toBeTransformedString = "unitTestingIdentityFieldNamingStrategyTest";

    @BeforeEach
    void setup() {
        sut = new IdentityFieldNamingStrategy();
    }

    @Test
    void identityFieldNamingStrategyLeavesFieldNameUnchanged() {
        final String expectedString = toBeTransformedString;
        final String actualString = sut.transform(toBeTransformedString);

        Assertions.assertEquals(expectedString, actualString);
    }
}
