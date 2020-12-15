package nl.jiankai.mapper.exceptions;

public class MappingFailedException extends RuntimeException {
    public MappingFailedException(final String message) {
        super(message);
    }
}
