package nl.jiankai.mapper.converters;

/**
 * This interface allows one to convert a value of class S to a value of class T.
 *
 * @param <S> source class
 * @param <T> target class
 * @author Jiankai Zheng (jk.zheng@hotmail.com)
 * @since 1.5.0
 */
public interface AttributeConverter<S, T> {
    /**
     * Converts a value of type S to type T
     *
     * @param value value to be converted
     * @return converted value
     */
    T convert(S value);

    /**
     * The source class that has to be mapped.
     *
     * @return the class type
     */
    Class<S> source();

    /**
     * The target class that to be mapped to.
     *
     * @return the class type
     */
    Class<T> target();
}
