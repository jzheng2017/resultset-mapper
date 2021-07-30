package nl.jiankai.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used on a field to let the {@link nl.jiankai.mapper.ResultSetMapper} know that it can skip this field when mapping.
 * This means that the {@link nl.jiankai.mapper.ResultSetMapper} will not try to retrieve the value from the {@link java.sql.ResultSet} for the annotated field.
 *
 * @author Jiankai Zheng (jk.zheng@hotmail.com)
 * @since 1.2.0
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Ignore {
}
