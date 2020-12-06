package nl.jiankai.mapper;

import nl.jiankai.annotations.Column;
import nl.jiankai.mapper.strategies.FieldNamingStrategy;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows the user to map a ResultSet to their desired model object
 *
 * @author Jiankai Zheng
 */
public class ResultSetMapper {
    private final Logger logger = LoggerFactory.getLogger(ResultSetMapper.class);
    private final FieldNamingStrategy fieldNamingStrategy;

    /**
     * Builds a ResultSetMapper with default IdentityFieldNamingStrategy
     */
    public ResultSetMapper() {
        this.fieldNamingStrategy = new IdentityFieldNamingStrategy();
        logger.info(String.format("No specific field naming strategy has been set. It will default to the %s field naming strategy.", this.fieldNamingStrategy));
    }

    /**
     * Builds a ResultSetMapper with a custom FieldNamingStrategy
     *
     * @param fieldNamingStrategy the field naming strategy to be used for mapping field names
     */
    public ResultSetMapper(final FieldNamingStrategy fieldNamingStrategy) {
        logger.info(String.format("The %s field naming strategy will be used for mapping.", fieldNamingStrategy));
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    /**
     * Maps the ResultSet to the desired class
     *
     * @param resultSet        the ResultSet returned by JDBC API
     * @param destinationClass the class to map to
     * @param <T>              the desired class
     * @return list of the mapped objects
     */
    public <T> List<T> map(final ResultSet resultSet, final Class<T> destinationClass) {
        final List<T> list = new ArrayList<>();

        try {
            if (resultSet == null || !resultSet.isBeforeFirst()) {
                logger.warn("An empty ResultSet has been passed in! Empty list will be returned.");
                return new ArrayList<>();
            }

            logger.info(String.format("Commencing mapping ResultSet to %s", destinationClass));
            final Map<String, Field> fields = getFields(destinationClass);


            while (resultSet.next()) {
                logger.trace(String.format("Adding new %s to the list", destinationClass));
                list.add(createObject(resultSet, destinationClass, fields));
            }
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            logger.error("Something has gone wrong! Exception: " + ex.getMessage());
        }

        logger.info(String.format("ResultSet has been successfully mapped to %s", destinationClass));
        return list;
    }

    /**
     * Get the field naming strategy the mapper is using to map field names
     * @return The field naming strategy to map field names
     */
    public FieldNamingStrategy getFieldNamingStrategy() {
        return this.fieldNamingStrategy;
    }


    /**
     * Create an instance of the destination class
     *
     * @param resultSet        the ResultSet returned by JDBC API
     * @param destinationClass the class to map to
     * @param fields           the mapped fields
     * @param <T>              the desired class
     * @return an instance of the destination class
     */
    private <T> T createObject(ResultSet resultSet, Class<T> destinationClass, Map<String, Field> fields) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        logger.trace(String.format("Constructing new %s instance", destinationClass));
        final T dto = destinationClass.getConstructor().newInstance();

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            final String key = entry.getKey();

            try {
                logger.trace(String.format("Retrieving %s from the ResultSet", key));
                final Object value = resultSet.getObject(key);
                logger.debug(String.format("Retrieval of %s has resulted to: %s", key, value));

                final Field field = entry.getValue();

                logger.trace(String.format("Setting the value %s to the field %s", value, field.getName()));
                field.set(dto, value);
            } catch (SQLException ex) {
                logger.warn(ex.getMessage());
            }
        }

        return dto;
    }

    /**
     * Get all declared fields of the destination class
     *
     * @param destinationClass the class to map to
     * @param <T>              the desired class
     * @return all declared fields mapped based on the field naming strategy
     */
    private <T> Map<String, Field> getFields(final Class<T> destinationClass) {
        final Map<String, Field> mappedFields = new HashMap<>();
        logger.trace(String.format("Retrieving all declared fields for class: %s", destinationClass));
        final Field[] declaredFields = destinationClass.getDeclaredFields();

        for (Field field : declaredFields) {
            mapFieldName(mappedFields, field);
        }

        return mappedFields;
    }

    /**
     * Maps the field based on the field naming strategy
     *
     * @param mappedFields the list of mapped fields
     * @param field        the field to be mapped
     */
    private void mapFieldName(Map<String, Field> mappedFields, Field field) {
        final String fieldName = field.getName();

        logger.trace(String.format("Retrieving @Column annotation for field: %s", fieldName));
        final Column columnAnnotation = field.getAnnotation(Column.class);

        logger.trace(String.format("Setting %s to accessibility to true", fieldName));
        field.setAccessible(true);

        if (columnAnnotation != null) {
            final String columnName = columnAnnotation.name();

            logger.trace(String.format("@Column annotation found for %s", fieldName));
            logger.trace(String.format("The field name strategy will be overruled. Mapping %s to %s", fieldName, columnName));
            mappedFields.put(columnName, field);
        } else {
            final String transformedName = fieldNamingStrategy.transform(fieldName);

            logger.trace(String.format("No @Column annotation found for %s", fieldName));
            logger.trace(String.format("Mapping %s to %s", fieldName, transformedName));
            mappedFields.put(transformedName, field);
        }
    }
}
