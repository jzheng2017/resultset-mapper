package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.SQLException;
import java.sql.Struct;

@Converter(autoApply = true)
public class StructToObjectArrayConverter implements AttributeConverter<Struct, Object[]> {

    @Override
    public Object[] convert(Struct value) {
        Object[] convertedValue = new Object[0];
    try {
        convertedValue = value.getAttributes();
    }
    catch (SQLException e){
        System.out.println(e.getCause().getMessage());
    }
        return convertedValue;
    }

    @Override
    public Class<Struct> source() {
        return Struct.class;
    }

    @Override
    public Class<Object[]> target() {
        return Object[].class;
    }
}
