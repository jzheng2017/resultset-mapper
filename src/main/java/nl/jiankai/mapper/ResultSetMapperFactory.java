package nl.jiankai.mapper;

import nl.jiankai.mapper.strategies.FieldNamingStrategy;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerUnderscoreFieldNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetMapperFactory {
    private final Logger logger = LoggerFactory.getLogger(ResultSetMapperFactory.class);

    public static ResultSetMapper getResultSetMapperLowerCaseUnderscore() {
        return getResultSetMapper(new LowerUnderscoreFieldNamingStrategy());
    }

    public static ResultSetMapper getResultSetMapperIdentity() {
        return getResultSetMapper(new IdentityFieldNamingStrategy());
    }

    private static ResultSetMapper getResultSetMapper(FieldNamingStrategy fieldNamingStrategy) {
        return new ResultSetMapper(fieldNamingStrategy);
    }
}
