package nl.jiankai.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to indicate it is a converter. This should be used on {@link nl.jiankai.mapper.converters.AttributeConverter}
 *
 * @author Jiankai Zheng
 * @since 1.5.0
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Converter {
    /**
     * Defines whether to auto apply the conversion when applicable. True equals auto applying.
     *
     * @return a boolean value whether to apply or not
     */
    boolean autoApply() default false;
}
