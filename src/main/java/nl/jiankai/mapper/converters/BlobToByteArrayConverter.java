package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Converter
public class BlobToByteArrayConverter implements AttributeConverter<Blob, byte[]> {
    private final Logger logger = Logger.getAnonymousLogger();

    @Override
    public byte[] convert(Blob value) {
        byte[] convertedValue = new byte[0];
    try {
        convertedValue = value.getBytes(1, (int) value.length());
    }
    catch (SQLException e){
        logger.log(Level.SEVERE, "an exception was thrown", e);
    }
        return convertedValue;
    }

    @Override
    public Class<Blob> source() {
        return Blob.class;
    }

    @Override
    public Class<byte[]> target() {
        return byte[].class;
    }
}
