package mapper;

import nl.jiankai.mapper.ResultSetMapper;
import nl.jiankai.mapper.ResultSetMapperFactory;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseDashesFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseUnderscoreFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ResultSetMapperFactoryTest {

    @Test
    void getResultSetMapperIdentityGetsConstructedCorrectly() {
        ResultSetMapper r = ResultSetMapperFactory.getResultSetMapperIdentity();

        Assertions.assertTrue(r.getFieldNamingStrategy() instanceof IdentityFieldNamingStrategy);

    }

    @Test
    void getResultSetMapperLowerCaseUnderscoreGetsConstructedCorrectly() {
        ResultSetMapper r = ResultSetMapperFactory.getResultSetMapperLowerCaseUnderscore();

        Assertions.assertTrue(r.getFieldNamingStrategy() instanceof LowerCaseUnderscoreFieldNamingStrategy);

    }

    @Test
    void getResultSetMapperLowerCaseDashesGetsConstructedCorrectly() {
        ResultSetMapper r = ResultSetMapperFactory.getResultSetMapperLowerCaseDashes();

        Assertions.assertTrue(r.getFieldNamingStrategy() instanceof LowerCaseDashesFieldNamingStrategy);
    }

    @Test
    void callingPrivateConstructorThrowsAssertionError() throws NoSuchMethodException {
        Constructor<ResultSetMapperFactory> constructor = ResultSetMapperFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        Assertions.assertTrue(exception.getTargetException() instanceof AssertionError);
    }
}
