package nl.jiankai.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to suppress logging warnings when retrieving value out of the {@link java.sql.ResultSet} has failed for some reason.
 * It can be used at class and field level. By using it on class level all warnings for all fields in that class will be suppressed.
 * By using it on field level only the individual logging warnings for the annotated field will be suppressed.
 *
 * @author Jiankai Zheng (jk.zheng@hotmail.com)
 * @since 1.1.0
 */
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SuppressWarnings {
}
