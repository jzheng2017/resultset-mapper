package nl.jiankai.mapper.converters;

import nl.jiankai.annotations.Converter;

import java.sql.Blob;
import java.sql.SQLException;

@Converter(autoApply = true)
public class BlobToByteArrayConverter implements AttributeConverter<Blob, byte[]> {

    @Override
    public byte[] convert(Blob value) {
        byte[] convertedValue = new byte[0];
    try {
        convertedValue = value.getBytes(1, (int) value.length());
    }
    catch (SQLException e){
        System.out.println(e.getCause().getMessage());
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
