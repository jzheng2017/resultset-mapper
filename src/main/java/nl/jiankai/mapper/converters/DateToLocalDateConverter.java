package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class DateToLocalDateConverter implements AttributeConverter<Date, LocalDate>{
    @Override
    public LocalDate convert(Date value) {
        return value.toLocalDate();
    }

    @Override
    public Class<Date> source() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> target() {
        return LocalDate.class;
    }
}
