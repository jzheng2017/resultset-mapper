package nl.jiankai.mapper;

import nl.jiankai.annotations.Column;
import nl.jiankai.annotations.Convert;
import nl.jiankai.annotations.Converter;
import nl.jiankai.annotations.Ignore;
import nl.jiankai.annotations.SuppressWarnings;
import nl.jiankai.mapper.converters.AttributeConverter;
import nl.jiankai.mapper.converters.DateToLocalDateConverter;
import nl.jiankai.mapper.converters.TimeToLocalTimeConverter;
import nl.jiankai.mapper.converters.TimestampToLocalDateTimeConverter;
import nl.jiankai.mapper.exceptions.MappingFailedException;
import nl.jiankai.mapper.strategies.FieldNamingStrategy;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class allows the user to map a ResultSet to their desired model object
 *
 * @author Jiankai Zheng
 */
public class ResultSetMapper {
    private final Logger logger = LoggerFactory.getLogger(ResultSetMapper.class);
    private final ClassCache classCache = new ClassCache();
    private final FieldNamingStrategy fieldNamingStrategy;
    private final Map<SourceAndTarget, AttributeConverter> attributeConvertersBySourceAndTarget = new HashMap<>();
    private final Map<Class, AttributeConverter> attributeConvertersByClass = new HashMap<>();
    private boolean hasClassLevelWarningSuppression;

    /**
     * Builds a ResultSetMapper with default IdentityFieldNamingStrategy
     */
    public ResultSetMapper() {
        registerAttributeConverters();
        this.fieldNamingStrategy = new IdentityFieldNamingStrategy();
        logger.info("No specific field naming strategy has been set. It will default to the {} field naming strategy.", this.fieldNamingStrategy);
    }

    /**
     * Builds a ResultSetMapper with a custom FieldNamingStrategy
     *
     * @param fieldNamingStrategy the field naming strategy to be used for mapping field names
     */
    public ResultSetMapper(final FieldNamingStrategy fieldNamingStrategy) {
        registerAttributeConverters();
        logger.info("The {} field naming strategy will be used for mapping.", fieldNamingStrategy);
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    /**
     * Get the field naming strategy the mapper is using to map field names
     *
     * @return The field naming strategy to map field names
     */
    public FieldNamingStrategy getFieldNamingStrategy() {
        return this.fieldNamingStrategy;
    }

    public void registerAttributeConverter(AttributeConverter attributeConverter) {
        putAttributeConverterInMap(attributeConverter);
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
        this.hasClassLevelWarningSuppression = classCache.isAnnotationPresent(destinationClass, SuppressWarnings.class);

        try {
            if (resultSet == null || !resultSet.isBeforeFirst()) {
                logger.warn("An empty ResultSet has been passed in! Empty list will be returned.");
                return new ArrayList<>();
            }

            logger.info("Commencing mapping ResultSet to {}", destinationClass);
            final Map<String, Field> fields = classCache.getFields(destinationClass);

            while (resultSet.next()) {
                logger.trace("Adding new {} to the list", destinationClass);
                list.add(createObject(resultSet, destinationClass, fields));
            }
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            logger.error("Something has gone wrong while mapping! Exception: " + ex.getMessage());
            throw new MappingFailedException("Something has gone wrong while mapping!", ex);
        }

        logger.info("ResultSet has been successfully mapped to {}", destinationClass);
        return list;
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
        logger.trace("Constructing new {} instance", destinationClass);
        final T dto = destinationClass.getConstructor().newInstance();

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            final String key = entry.getKey();
            final Field field = entry.getValue();

            try {
                logger.trace("Fetching '{}' from the ResultSet", key);
                final Object value = resultSet.getObject(key);
                logger.debug("Retrieval of '{}' has resulted to: {}", key, value);

                logger.trace("Setting the value '{}' to the field: {}", value, field.getName());
                field.set(dto, tryConvertValue(field, value));
                logger.trace("Value set successfully.");
            } catch (SQLException ex) {
                final boolean fieldWarningsNotSuppressed = !(hasClassLevelWarningSuppression || classCache.isFieldAnnotationPresent(field, SuppressWarnings.class));

                if (fieldWarningsNotSuppressed) {
                    logger.warn("Something went wrong while trying to construct the object.", ex);
                }
            }
        }

        return dto;
    }

    /**
     * Tries to convert a value of one type to another if the appropriate {@link AttributeConverter} for that exists.
     *
     * @param value the value to be converted
     * @return a value that is either converter or not
     */
    private Object tryConvertValue(Field field, Object value) {
        AttributeConverter attributeConverter;
        logger.trace("Finding annotation {}", Convert.class);
        Convert convertAnnotation = classCache.getFieldAnnotation(field, Convert.class);

        if (convertAnnotation != null) {
            logger.trace("{} annotation found!", Converter.class);
            logger.trace("Fetching attribute converter {}", convertAnnotation.converter());
            attributeConverter = attributeConvertersByClass.get(convertAnnotation.converter());

            if (attributeConverter != null) {
                logger.trace("Attribute converter {} found!", convertAnnotation.converter());
                logger.trace("Converting value");

                return attributeConverter.convert(value);
            }
        } else {
            logger.trace("{} annotation not found", Converter.class);
            logger.trace("Fetching attribute converter for {} to {}", value.getClass(), field.getType());
            attributeConverter = attributeConvertersBySourceAndTarget.get(new SourceAndTarget(value.getClass(), field.getType()));

            if (attributeConverter != null) {
                logger.trace("Attribute converter {} found!", attributeConverter.getClass());
                logger.trace("Finding annotation {}", Converter.class);
                Converter converterAnnotation = classCache.getAnnotation(attributeConverter.getClass(), Converter.class);
                if (converterAnnotation == null) {
                    logger.trace("An attribute converter has been found but no {} annotation was present. Therefore converting will not be done.", Converter.class);
                } else if (converterAnnotation.autoApply()) {
                    logger.trace("Annotation {} found and autoApply is on", converterAnnotation.getClass());
                    logger.trace("Converting value");
                    return attributeConverter.convert(value);
                }
            }
        }

        return value;
    }

    /**
     * Registers all out of the box {@link AttributeConverter}
     */
    private void registerAttributeConverters() {
        var timestampToLocalDateTimeConverter = new TimestampToLocalDateTimeConverter();
        var dateToLocalDateConverter = new DateToLocalDateConverter();
        var timeToLocalTimeConverter = new TimeToLocalTimeConverter();

        putAttributeConverterInMap(timestampToLocalDateTimeConverter);
        putAttributeConverterInMap(dateToLocalDateConverter);
        putAttributeConverterInMap(timeToLocalTimeConverter);
    }

    private void putAttributeConverterInMap(AttributeConverter attributeConverter) {
        logger.trace("Registering attribute converter {}", attributeConverter.getClass());
        attributeConvertersBySourceAndTarget.put(new SourceAndTarget(attributeConverter.source(), attributeConverter.target()), attributeConverter);
        attributeConvertersByClass.put(attributeConverter.getClass(), attributeConverter);
    }


    /**
     * This class fetches the class reflection data. After fetching it for the first time it will keep the class data in the cache so that it can retrieved later if desired.
     *
     * @author Jiankai Zheng (jk.zheng@hotmail.com)
     * @since 1.6.0
     */
    private class ClassCache {
        private final Logger logger = LoggerFactory.getLogger(ClassCache.class);
        private final Map<Class, Map<String, Field>> cachedClassFields = new HashMap<>();
        private final Map<Class, Map<Class, Annotation>> cachedClassAnnotations = new HashMap<>();
        private final Map<Field, Map<Class, Annotation>> cachedFieldAnnotations = new HashMap<>();

        /**
         * Get annotation of a class from cache. If it's not present in the cache it will try to fetch it through reflection.
         *
         * @param classToSearchFor      the class you want the annotation for
         * @param annotationToSearchFor the annotation you want
         * @return the annotation
         */
        public <T> T getAnnotation(Class classToSearchFor, Class<T> annotationToSearchFor) {
            Map<Class, Annotation> cachedAnnotations = cachedClassAnnotations.get(classToSearchFor);

            if (cachedAnnotations == null) {
                cachedAnnotations = Arrays.stream(classToSearchFor.getAnnotations()).collect(Collectors.toMap(Annotation::annotationType, annotation -> annotation));
                cachedClassAnnotations.put(classToSearchFor, cachedAnnotations);
            }

            return (T) cachedClassAnnotations.get(classToSearchFor).get(annotationToSearchFor);
        }

        /**
         * Get annotation of a field from cache. If it's not present in the cache it will try to fetch it through reflection.
         *
         * @param fieldToSearchFor      the field you want the annotation for
         * @param annotationToSearchFor the annotation you want
         * @return the annotation
         */
        public <T> T getFieldAnnotation(Field fieldToSearchFor, Class<T> annotationToSearchFor) {
            Map<Class, Annotation> cachedAnnotations = cachedFieldAnnotations.get(fieldToSearchFor);

            if (cachedAnnotations == null) {
                cachedAnnotations = Arrays.stream(fieldToSearchFor.getAnnotations()).collect(Collectors.toMap(Annotation::annotationType, annotation -> annotation));
                cachedFieldAnnotations.put(fieldToSearchFor, cachedAnnotations);
            }

            return (T) cachedFieldAnnotations.get(fieldToSearchFor).get(annotationToSearchFor);
        }

        /**
         * Tells whether an annotation is present for a particular class.
         *
         * @param classToSearchFor      the class you want to look at
         * @param annotationToSearchFor the annotation you want to find
         * @return whether it's present or not
         */
        public boolean isAnnotationPresent(Class classToSearchFor, Class annotationToSearchFor) {
            return getAnnotation(classToSearchFor, annotationToSearchFor) != null;
        }

        /**
         * Tells whether an annotation is present for a particular field.
         *
         * @param fieldToSearchFor      the field you want to look at
         * @param annotationToSearchFor the annotation you want to find
         * @return whether it's present or not
         */
        public boolean isFieldAnnotationPresent(Field fieldToSearchFor, Class annotationToSearchFor) {
            return getFieldAnnotation(fieldToSearchFor, annotationToSearchFor) != null;
        }

        /**
         * Get all declared fields of the destination class
         *
         * @param classToSearchFor the class you want the fields of
         * @param <T>              the desired class
         * @return all declared fields mapped based on the field naming strategy
         */
        public <T> Map<String, Field> getFields(final Class<T> classToSearchFor) {
            logger.trace("Trying to fetch {} fields from cache..", classToSearchFor);
            Map<String, Field> fields = cachedClassFields.get(classToSearchFor);

            if (fields == null) {
                logger.trace("{} is not available in the cache.", classToSearchFor);
                fields = getFieldsAndPutInCache(classToSearchFor);
            }

            return Map.copyOf(fields);
        }

        /**
         * Get all fields from the specified class and put it in the cache and return the list of fields.
         *
         * @param clazz the class you want the fields of
         * @param <T>   the type of class
         * @return the fields of the class
         */
        private <T> Map<String, Field> getFieldsAndPutInCache(Class<T> clazz) {
            final Map<String, Field> fields = new HashMap<>();

            logger.trace("Fetching all declared fields for class: {}", clazz);
            final List<Field> declaredFields = getFields(new ArrayList<>(), clazz);


            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(Ignore.class)) {
                    mapFieldName(fields, field);
                }
            }

            logger.trace("{} fetched. Saving to cache.", clazz);
            cachedClassFields.put(clazz, fields);
            return fields;
        }

        /**
         * Get all public, protected and private fields recursively (including fields from the super classes)
         *
         * @param fields a list of fields
         * @param type   the destination class
         * @return list of all fields of the passed in class
         * @since 1.4.0
         */
        private List<Field> getFields(List<Field> fields, Class<?> type) {
            logger.trace("Adding all declared fields from class {}", type);
            fields.addAll(Arrays.asList(type.getDeclaredFields()));

            if (type.getSuperclass() != null) {
                logger.trace("Retrieving declared fields from class {}", type);
                getFields(fields, type.getSuperclass());
            }

            return fields;
        }


        /**
         * Maps the field based on the field naming strategy
         *
         * @param mappedFields the list of mapped fields
         * @param field        the field to be mapped
         */
        private void mapFieldName(Map<String, Field> mappedFields, Field field) {
            final String fieldName = field.getName();

            logger.trace("Fetching @Column annotation for field '{}'", fieldName);
            final Column columnAnnotation = field.getAnnotation(Column.class);

            logger.trace("Setting '{}' accessibility to true", fieldName);
            field.setAccessible(true);

            if (columnAnnotation != null) {
                final String columnName = columnAnnotation.name();

                logger.trace("@Column annotation found for '{}'", fieldName);
                logger.trace("The field name strategy will be overruled. Mapping '{}' to '{}'", fieldName, columnName);
                mappedFields.put(columnName, field);
            } else {
                final String transformedName = fieldNamingStrategy.transform(fieldName);

                logger.trace("No @Column annotation found for '{}'", fieldName);
                logger.trace("Mapping '{}' to '{}'", fieldName, transformedName);
                mappedFields.put(transformedName, field);
            }
        }
    }

    private class SourceAndTarget {
        private final Class source;
        private final Class target;

        public SourceAndTarget(Class source, Class target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SourceAndTarget that = (SourceAndTarget) o;
            return Objects.equals(source, that.source) && Objects.equals(target, that.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }
}
