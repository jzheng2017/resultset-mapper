package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Ref;
import java.sql.SQLException;

@Converter(autoApply = true)
public class RefToObjectConverter implements AttributeConverter<Ref, Object> {

    @Override
    public Object convert(Ref value) {
        Object convertedValue = null;
    try {
        convertedValue = value.getObject();
    }
    catch (SQLException e){
        System.out.println(e.getCause().getMessage());
    }
        return convertedValue;
    }

    @Override
    public Class<Ref> source() {
        return Ref.class;
    }

    @Override
    public Class<Object> target() {
        return Object.class;
    }
}
