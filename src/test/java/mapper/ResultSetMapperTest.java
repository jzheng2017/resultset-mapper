package mapper;

import mapper.mocks.*;
import nl.jiankai.annotations.Converter;
import nl.jiankai.mapper.ResultSetMapper;
import nl.jiankai.mapper.converters.AttributeConverter;
import nl.jiankai.mapper.exceptions.MappingFailedException;
import nl.jiankai.mapper.strategies.IdentityFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseDashesFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseUnderscoreFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

public class ResultSetMapperTest {
    private ResultSetMapper sut;
    @Mock
    private ResultSet mockedResultSet;
    @Mock
    private SQLException mockedException;

    @BeforeEach
    void setup() {
        sut = new ResultSetMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resultSetMapperReturnsEmptyListWhenNullIsPassedIn() {
        List<User> returnedList = sut.map(null, User.class);

        Assertions.assertTrue(returnedList.isEmpty());
    }

    @Test
    void resultSetMapperReturnsEmptyListWhenEmptyResultSetIsPassedIn() {
        List<User> returnedList = sut.map(mockedResultSet, User.class);

        Assertions.assertTrue(returnedList.isEmpty());
    }

    @Test
    void resultSetMapperReturnsListOfUserWhenResultSetNotEmpty() {
        populatedResultSetIdentity();
        List<User> users = sut.map(mockedResultSet, User.class);

        Assertions.assertFalse(users.isEmpty());
    }

    @Test
    void resultSetMapperIdentityMapsValuesCorrectly() {
        populatedResultSetIdentity();
        List<User> users = sut.map(mockedResultSet, User.class);

        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals("firstName", users.get(0).getFirstName());
        Assertions.assertEquals("lastName", users.get(0).getLastName());
        Assertions.assertEquals("email", users.get(0).getEmail());
    }

    @Test
    void resultSetMapperLowerCaseUnderscoreMapsValuesCorrectly() {
        populatedResultSetLowerCaseUnderscore();
        List<User> users = sut.map(mockedResultSet, User.class);

        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals("first_name", users.get(0).getFirstName());
        Assertions.assertEquals("last_name", users.get(0).getLastName());
        Assertions.assertEquals("email", users.get(0).getEmail());
        Assertions.assertEquals("birth_date", users.get(0).getBirthDate());
    }

    @Test
    void resultSetMapperLowerCaseDashesMapsValuesCorrectly() {
        populatedResultSetLowerCaseDashes();
        List<User> users = sut.map(mockedResultSet, User.class);

        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals("first_name", users.get(0).getFirstName());
        Assertions.assertEquals("last_name", users.get(0).getLastName());
        Assertions.assertEquals("email", users.get(0).getEmail());
        Assertions.assertEquals("birth-date", users.get(0).getBirthDate());
    }

    @Test
    void resultSetMapperMapsCorrectlyWhenOverriddenWithColumnAnnotation() {
        populatedResultSetOverrideIdentity();
        List<OverrideObject> overrides = sut.map(mockedResultSet, OverrideObject.class);

        Assertions.assertEquals("overridden_name", overrides.get(0).getOverriddenName());
    }

    @Test
    void resultSetMapperMapsCorrectlyWhenUsingIgnoreAnnotation() {
        populatedResultSetIgnoreIdentity();
        List<IgnoreObject> overrides = sut.map(mockedResultSet, IgnoreObject.class);

        Assertions.assertNull(overrides.get(0).ignored);
        Assertions.assertNotNull(overrides.get(0).notIgnored);
    }

    @Test
    void resultSetMapperHandlesExceptionsCorrectly() throws SQLException {
        when(mockedResultSet.isBeforeFirst()).thenThrow(SQLException.class);

        Assertions.assertThrows(MappingFailedException.class, () -> sut.map(mockedResultSet, OverrideObject.class));
    }

    @Test
    void suppressWarningsAnnotationOnClassLevelStopsLoggingForAllFields() throws SQLException {
        when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockedResultSet.isBeforeFirst()).thenReturn(true);
        when(mockedResultSet.getObject("test")).thenThrow(mockedException);
        when(mockedResultSet.getObject("test2")).thenThrow(mockedException);

        sut.map(mockedResultSet, SuppressOnClassLevel.class);
        verifyNoInteractions(mockedException);
    }

    @Test
    void suppressWarningsAnnotationOnFieldLevelStopsLoggingForAnnotatedFields() throws SQLException {
        when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockedResultSet.isBeforeFirst()).thenReturn(true);
        when(mockedResultSet.getObject("test")).thenThrow(mockedException);
        when(mockedResultSet.getObject("test2")).thenThrow(mockedException);

        sut.map(mockedResultSet, SuppressOnFieldLevel.class);

        verify(mockedException).printStackTrace(any(PrintWriter.class));
    }

    @Test
    void resultSetMapperReturnsCorrectMappedObjectWhenHasBaseClassAttributes() {
        populatedResultSetBaseChildClassIdentity();

        List<Child> children = sut.map(mockedResultSet, Child.class);

        Assertions.assertEquals(10, children.get(0).getBaseAttribute());
        Assertions.assertEquals("overridden_name", children.get(0).getOverriddenBaseAttribute());
        Assertions.assertEquals("childAttribute", children.get(0).getChildAttribute());

    }

    @Test
    void resultSetMapperCorrectlyRegistersAnnotationsFromBaseClass() {
        populatedResultSetBaseChildClassIdentity();

        List<Child> children = sut.map(mockedResultSet, Child.class);
        Assertions.assertEquals("overridden_name", children.get(0).getOverriddenBaseAttribute());
    }

    @Test
    void resultSetMapperCorrectlyAndAutomaticallyConvertsValueOfDifferentType() {
        populatedResultSetPersonClassIdentity();

        List<Person> persons = sut.map(mockedResultSet, Person.class);

        Assertions.assertNotNull(persons.get(0).getChildRegistered());
        Assertions.assertNotNull(persons.get(0).getDateOfBirth());
        Assertions.assertNotNull(persons.get(0).getTimeOfBirth());
    }

    @Test
    void resultSetMapperCorrectlyHandlesConvertAnnotation(){
        populatedResultSetPersonConvertAnnotationsClassIdentity();

        List<PersonConvertAnnotations> persons = sut.map(mockedResultSet, PersonConvertAnnotations.class);

        Assertions.assertNotNull(persons.get(0).getChildRegistered());
        Assertions.assertNotNull(persons.get(0).getDateOfBirth());
        Assertions.assertNotNull(persons.get(0).getTimeOfBirth());
    }

    @Test
    void resultSetMapperCorrectlyHandlesNullValues() {
        populatedResultSetWithNullValues();
        List<User> users = sut.map(mockedResultSet, User.class);

        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals("first_name", users.get(0).getFirstName());
        Assertions.assertEquals("last_name", users.get(0).getLastName());
        Assertions.assertNull(users.get(0).getEmail());
        Assertions.assertEquals("birthDate", users.get(0).getBirthDate());
    }


    private void populatedResultSetBaseChildClassIdentity() {
        sut = new ResultSetMapper();
        try {
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("baseAttribute")).thenReturn(10);
            when(mockedResultSet.getObject("childAttribute")).thenReturn("childAttribute");
            when(mockedResultSet.getObject("overridden_name")).thenReturn("overridden_name");
            when(mockedResultSet.getObject("overridden_name")).thenReturn("overridden_name");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void populatedResultSetPersonClassIdentity() {
        sut = new ResultSetMapper();
        try {
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("dateOfBirth")).thenReturn(Date.valueOf(LocalDate.now()));
            when(mockedResultSet.getObject("timeOfBirth")).thenReturn(Time.valueOf(LocalTime.now()));
            when(mockedResultSet.getObject("childRegistered")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatedResultSetPersonConvertAnnotationsClassIdentity() {
        sut = new ResultSetMapper();
        sut.registerAttributeConverter(new LocalDateTimeToLocalDateConverter());
        try {
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("dateOfBirth")).thenReturn(LocalDateTime.now());
            when(mockedResultSet.getObject("timeOfBirth")).thenReturn(Time.valueOf(LocalTime.now()));
            when(mockedResultSet.getObject("childRegistered")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatedResultSetOverrideIdentity() {
        sut = new ResultSetMapper();
        try {
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("overridden_name")).thenReturn("overridden_name");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void populatedResultSetIgnoreIdentity() {
        sut = new ResultSetMapper();
        try {
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("notIgnored")).thenReturn("notIgnored");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void populatedResultSetIdentity() {
        try {
            sut = new ResultSetMapper();
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("id")).thenReturn(1);
            when(mockedResultSet.getObject("first_name")).thenReturn("firstName");
            when(mockedResultSet.getObject("last_name")).thenReturn("lastName");
            when(mockedResultSet.getObject("email")).thenReturn("email");
            when(mockedResultSet.getObject("birthDate")).thenReturn("birthDate");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatedResultSetLowerCaseUnderscore() {
        try {
            sut = new ResultSetMapper(new LowerCaseUnderscoreFieldNamingStrategy());
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("id")).thenReturn(1);
            when(mockedResultSet.getObject("first_name")).thenReturn("first_name");
            when(mockedResultSet.getObject("last_name")).thenReturn("last_name");
            when(mockedResultSet.getObject("email")).thenReturn("email");
            when(mockedResultSet.getObject("birth_date")).thenReturn("birth_date");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatedResultSetLowerCaseDashes() {
        try {
            sut = new ResultSetMapper(new LowerCaseDashesFieldNamingStrategy());
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("id")).thenReturn(1);
            when(mockedResultSet.getObject("first_name")).thenReturn("first_name");
            when(mockedResultSet.getObject("last_name")).thenReturn("last_name");
            when(mockedResultSet.getObject("email")).thenReturn("email");
            when(mockedResultSet.getObject("birth-date")).thenReturn("birth-date");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatedResultSetWithNullValues() {
        try {
            sut = new ResultSetMapper(new IdentityFieldNamingStrategy());
            when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockedResultSet.isBeforeFirst()).thenReturn(true);
            when(mockedResultSet.getObject("id")).thenReturn(1);
            when(mockedResultSet.getObject("first_name")).thenReturn("first_name");
            when(mockedResultSet.getObject("last_name")).thenReturn("last_name");
            when(mockedResultSet.getObject("email")).thenReturn(null);
            when(mockedResultSet.getObject("birthDate")).thenReturn("birthDate");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Converter
    public class LocalDateTimeToLocalDateConverter implements AttributeConverter<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime value) {
            return value.toLocalDate();
        }

        @Override
        public Class<LocalDateTime> source() {
            return LocalDateTime.class;
        }

        @Override
        public Class<LocalDate> target() {
            return LocalDate.class;
        }
    }
}
