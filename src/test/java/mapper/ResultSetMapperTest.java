package mapper;

import mapper.mocks.*;
import nl.jiankai.mapper.ResultSetMapper;
import nl.jiankai.mapper.exceptions.MappingFailedException;
import nl.jiankai.mapper.strategies.LowerCaseDashesFieldNamingStrategy;
import nl.jiankai.mapper.strategies.LowerCaseUnderscoreFieldNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        verify(mockedException, times(0)).getMessage();
    }

    @Test
    void suppressWarningsAnnotationOnFieldLevelStopsLoggingForAnnotatedFields() throws SQLException {
        when(mockedResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockedResultSet.isBeforeFirst()).thenReturn(true);
        when(mockedResultSet.getObject("test")).thenThrow(mockedException);
        when(mockedResultSet.getObject("test2")).thenThrow(mockedException);

        sut.map(mockedResultSet, SuppressOnFieldLevel.class);
        verify(mockedException).getMessage();
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
}
