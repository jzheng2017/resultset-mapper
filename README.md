[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/jzheng2017/resultset-mapper.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jzheng2017/resultset-mapper/context:java)
# ResultSet Mapper
ResultSet Mapper allows you to map a `ResultSet` to a desired java object by passing in its type.

## Example usages

### Model object
Define a java object you want to map to. Use the `@Column` annotation override the `FieldNamingStrategy`
```java
public class User {
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
}
```

### Map `ResultSet` to desired model object 
To map a `ResultSet` you need an instance of `ResultSetMapper`. The library provides a factory class `ResultSetMapperFactory` that serves `ResultSetMapper` with different `FieldNamingStrategy`. The `ResultSetMapper` defaults to the `IdentityFieldNamingStrategy`.
```java
ResultSetMapper r = ResultSetMapperFactory.getResultSetMapperIdentity();

List<User> users = r.map(resultSet, User.class);
```

### Field naming strategies
The library provides out of the box a few field naming strategies.
#### IdentityFieldNamingStrategy
This strategy leaves the field names unchanged.
#### LowercaseUnderscoreFieldNamingStrategy
This strategy maps the field names to lowercase underscore. For instance `firstName` would map to `first_name`.
#### Custom field naming strategy
It is possible to make your own field naming strategy. It is done by implementing the `FieldNamingStrategy` interface. It has one function `transform` which transforms the original field name.
