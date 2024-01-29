package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Clob;
import java.sql.SQLException;

@Converter(autoApply = true)
public class ClobToStringConverter implements AttributeConverter<Clob, String> {

    @Override
    public String convert(Clob value) {
        String convertedValue = null;
    try {
        convertedValue = value.getSubString(1, (int) value.length());
    }
    catch (SQLException e){
        System.out.println(e.getCause().getMessage());
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
