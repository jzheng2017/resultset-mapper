package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Array;
import java.sql.SQLException;

@Converter(autoApply = true)
public class ArrayToStringArrayConverter implements AttributeConverter<Array, String[]> {

    @Override
    public String[] convert(Array value) {
        String[] convertedValue = new String[0];
    try {
        convertedValue = (String[]) value.getArray();
    }
    catch (SQLException e){
        System.out.println(e.getCause().getMessage());
    }
        return convertedValue;
    }

    @Override
    public Class<Array> source() {
        return Array.class;
    }

    @Override
    public Class<String[]> target() {
        return String[].class;
    }
}
