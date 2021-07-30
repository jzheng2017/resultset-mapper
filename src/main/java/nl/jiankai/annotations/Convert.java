package nl.jiankai.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on a class field to specify which {@link nl.jiankai.mapper.converters.AttributeConverter} has to be used on it. It will override default converters if autoApply = true.
 *
 * @author Jiankai Zheng (jk.zheng@hotmail.com)
 * @since 1.5.0
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Convert {
    /**
     * Defines which {@link nl.jiankai.mapper.converters.AttributeConverter} to use.
     *
     * @return the class type of the
     */
    Class converter();
}
