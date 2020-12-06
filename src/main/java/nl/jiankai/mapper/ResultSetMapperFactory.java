package nl.jiankai.mapper;

import nl.jiankai.mapper.strategies.FieldNamingStrategy;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseDashesFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseUnderscoreFieldNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetMapperFactory {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetMapperFactory.class);

    private ResultSetMapperFactory() {
        throw new AssertionError("The ResultSetMapperFactory should not be instantiated.");
    }

    public static ResultSetMapper getResultSetMapperLowerCaseUnderscore() {
        return getResultSetMapper(new LowerCaseUnderscoreFieldNamingStrategy());
    }

    public static ResultSetMapper getResultSetMapperIdentity() {
        return getResultSetMapper(new IdentityFieldNamingStrategy());
    }

    public static ResultSetMapper getResultSetMapperLowerCaseDashes() {
        return getResultSetMapper(new LowerCaseDashesFieldNamingStrategy());
    }

    private static ResultSetMapper getResultSetMapper(FieldNamingStrategy fieldNamingStrategy) {
        logger.trace("Constructing ResultSetMapper with the {} strategy.", fieldNamingStrategy);
        return new ResultSetMapper(fieldNamingStrategy);
    }
}
