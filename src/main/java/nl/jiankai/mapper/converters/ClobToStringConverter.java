package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Converter(autoApply = true)
public class ClobToStringConverter implements AttributeConverter<Clob, String> {
    private final Logger logger = Logger.getAnonymousLogger();

    @Override
    public String convert(Clob value) {
        String convertedValue = null;
    try {
        convertedValue = value.getSubString(1, (int) value.length());
    }
    catch (SQLException e){
        logger.log(Level.SEVERE, "an exception was thrown", e);
    }
        return convertedValue;
    }

    @Override
    public Class<Clob> source() {
        return Clob.class;
    }

    @Override
    public Class<String> target() {
        return String.class;
    }
}
