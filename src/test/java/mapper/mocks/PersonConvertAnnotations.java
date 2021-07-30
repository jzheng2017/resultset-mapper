package mapper.mocks;

import mapper.ResultSetMapperTest;
import nl.jiankai.annotations.Convert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PersonConvertAnnotations {
    @Convert(converter = ResultSetMapperTest.LocalDateTimeToLocalDateConverter.class)
    private LocalDate dateOfBirth;
    private LocalTime timeOfBirth;
    private LocalDateTime childRegistered;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalTime getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfBirth(LocalTime timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public LocalDateTime getChildRegistered() {
        return childRegistered;
    }

    public void setChildRegistered(LocalDateTime childRegistered) {
        this.childRegistered = childRegistered;
    }
}
