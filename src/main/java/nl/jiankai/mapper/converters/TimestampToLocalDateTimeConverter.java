package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class TimestampToLocalDateTimeConverter implements AttributeConverter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime convert(Timestamp value) {
        return value.toLocalDateTime();
    }

    @Override
    public Class<Timestamp> source() {
        return Timestamp.class;
    }

    @Override
    public Class<LocalDateTime> target() {
        return LocalDateTime.class;
    }
}
