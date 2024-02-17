package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Array;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Converter(autoApply = true)
public class ArrayToObjectArrayConverter implements AttributeConverter<Array, Object[]> {
    private final Logger logger = Logger.getAnonymousLogger();

    @Override
    public Object[] convert(Array value) {
        Object[] convertedValue = null;
    try {
        convertedValue = (Object[]) value.getArray();
    }
    catch (SQLException e){
        logger.log(Level.SEVERE, "an exception was thrown", e);
    }
        return convertedValue;
    }

    @Override
    public Class<Array> source() {
        return Array.class;
    }

    @Override
    public Class<Object[]> target() {
        return Object[].class;
    }
}
