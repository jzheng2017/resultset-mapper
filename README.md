[![Maven Central](https://img.shields.io/maven-central/v/nl.jiankai/resultset-mapper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22nl.jiankai%22%20AND%20a:%22resultset-mapper%22) [![Build Status](https://travis-ci.com/jzheng2017/resultset-mapper.svg?branch=main)](https://travis-ci.com/jzheng2017/resultset-mapper) [![Coverage Status](https://coveralls.io/repos/github/jzheng2017/resultset-mapper/badge.svg?branch=main)](https://coveralls.io/github/jzheng2017/resultset-mapper?branch=main) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/jzheng2017/resultset-mapper.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jzheng2017/resultset-mapper/context:java)
# ResultSet Mapper
ResultSet Mapper is a small lightweight library that allows you to map a `ResultSet` object to a desired java object by passing in its type.
## Example usages
### Model object
Define a java object you want to map to. Use the `@Column` annotation to override the `FieldNamingStrategy`
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

**Note**: The provided strategies assumes you use `camelCase` for your field names. It does not work if you use other naming styles. If this does not conform to your naming style, you have to implement your own field naming strategy.
#### IdentityFieldNamingStrategy
This strategy leaves the field names unchanged.
#### LowerCaseUnderscoreFieldNamingStrategy
This strategy maps the field names to lowercase with underscores. For instance `firstName` would map to `first_name`.
#### LowerCaseDashesFieldNamingStrategy
This strategy maps the field names to lowercase with dashes. For instance `firstName` would map to `first-name`.
#### Custom field naming strategy
It is possible to make your own field naming strategy. It is done by implementing the `FieldNamingStrategy` interface. It has one function `transform` which transforms the original field name. The concrete `FieldNamingStrategy` implementation can then be injected through the constructor.
```java
ResultSetMapper r = new ResultSetMapper(new CustomFieldNamingStrategy());
```
## Why use this library?
This library makes it easy to map to a `ResultSet` to your desired java model object. It can be done with only 1 line of code! It saves you a lot of duplicate code when mapping every query.
```java
List<User> users = new ArrayList();

while (resultSet.next()){
    User user = new User();
    user.setId(resultSet.getInt("id");
    user.setFirstName(resultSet.getString("first_name"));
    user.setLastName(resultSet.getString("last_name"));
    user.setEmail(resultSet.getString("email"));
    
    users.add(user);
}
```
vs
```java
List<User> users = r.map(resultSet, User.class);
```
Way more cleaner, right? Imagine doing the first example for every different query, that would be a lot of code.. 

The library also takes care of all the exception handling and provides very extensive logging, making it very easy to spot errors when it occurs.
## Installation
### Maven
```xml
<dependency>
  <groupId>nl.jiankai</groupId>
  <artifactId>resultset-mapper</artifactId>
  <version>1.0.4</version>
</dependency>
```
### Gradle
```gradle
implementation 'nl.jiankai:resultset-mapper:1.0.4'
```
## License
See the [LICENSE](https://github.com/jzheng2017/resultset-mapper/blob/main/LICENSE) file for the license rights and limitations (MIT).
## Java version
The library uses Java 11.
