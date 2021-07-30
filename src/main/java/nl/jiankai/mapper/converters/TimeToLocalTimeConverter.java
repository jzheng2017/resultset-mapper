package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Time;
import java.time.LocalTime;

@Converter(autoApply = true)
public class TimeToLocalTimeConverter implements AttributeConverter<Time, LocalTime> {
    @Override
    public LocalTime convert(Time value) {
        return value.toLocalTime();
    }

    @Override
    public Class<Time> source() {
        return Time.class;
    }

    @Override
    public Class<LocalTime> target() {
        return LocalTime.class;
    }
}
