package mapper.mocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Person {
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
