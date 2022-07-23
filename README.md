# WARNING

This is **NOT** about **WETHER** this should be done, it's about **HOW** to do it, when you're facing that situation.

In my book, the best approach is a micro-service implementation where each service gets to deal with its own (and usually single) data source, or consuming other micro-services (abd each of those dealing with their own data source).

# Overview

This project shows how to handle `multiple data sources` with `Spring Boot JPA`.

This application deals with 2 `core`: `coreA` and `coreB`.

The `coreA` represents a data source where user information is stored. It holds the definition of [User](src/main/java/com/example/springboot/core/coreA/be/User.java) business entity (BE) as well as [UserRepository](src/main/java/com/example/springboot/core/coreA/dao/UserRepository.java) data access object (DAO).

The `coreB` represents a data source where other kind of information is stored: address and the mapping between users (just id) and addresses. It holds the definition of BEs [Address](src/main/java/com/example/springboot/core/coreB/be/Address.java), [UserAddress](src/main/java/com/example/springboot/core/coreB/be/UserAddress.java), and their respective DAOs: [AddressRepository](src/main/java/com/example/springboot/core/coreB/dao/AddressRepository.java) and [UserAddressRepository](src/main/java/com/example/springboot/core/coreB/dao/UserAddressRepository.java).

A service [MyService](src/main/java/com/example/springboot/service/MyService.java) is also provided to see how it bridges the 2 data sources into a service-level model (`UserAddressList`) using data access objects (`CrudRepository`s) from each core.

# Problems

There are 2 major problems when dealing with multiple data sources within the same Spring Boot application:

* How to define multiple data sources in Java and in `application.properties`
* How to setup tests and initialize data sources prior to running the tests

# Demo

H2 database is used as a fast, in-memory database to run the tests. 


```bash
./mvnw test
```

You'll notice in the output that 2 data sources are well defined and each used and initialized as intended.
The first one will create the `User` model:

```
2022-07-22 22:24:08.691  INFO 27081 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2022-07-22 22:24:08.720  INFO 27081 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 5.6.10.Final
2022-07-22 22:24:08.820  INFO 27081 --- [           main] o.hibernate.annotations.common.Version   : HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
2022-07-22 22:24:08.901  INFO 27081 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
Hibernate: 
    
    create table users (
       id bigint generated by default as identity,
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        middle_name varchar(255),
        phone_number varchar(255),
        primary key (id)
    )
2022-07-22 22:24:09.262  INFO 27081 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2022-07-22 22:24:09.267  INFO 27081 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
```

The second data source then initialized with `Address` and `UserAddress` models:

```
2022-07-22 22:24:09.302  INFO 27081 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2022-07-22 22:24:09.308  INFO 27081 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-07-22 22:24:09.320  INFO 27081 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2022-07-22 22:24:09.323  INFO 27081 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
Hibernate: 
    
    create table addresses (
       id bigint generated by default as identity,
        address1 varchar(255),
        address2 varchar(255),
        city varchar(255),
        state varchar(255),
        zip_code varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table users_addresses (
       id bigint generated by default as identity,
        home_address_flag boolean not null,
        user_id bigint not null,
        address_id bigint,
        primary key (id)
    )
Hibernate: 
    
    alter table users_addresses 
       add constraint FKkiudnofuhukgsdoy6i4ldg43e 
       foreign key (address_id) 
       references addresses
2022-07-22 22:24:09.417  INFO 27081 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2022-07-22 22:24:09.417  INFO 27081 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
```

One way to notice the wrong approach is used when for each data source, all models are generated.
To see what that looks like, all we need to do is replace `packages` value in the definition of the `LocalContainerEntityManagerFactoryBean` bean for each data source:

* for `coreA` we turn `com.example.springboot.core.coreA` into `com.example.springboot.core` in [CoreAConfig.java](src/main/java/com/example/springboot/config/CoreAConfig.java),
* and for `coreB` we turn `com.example.springboot.core.coreB` into `com.example.springboot.core` in  [CoreAConfig.java](src/main/java/com/example/springboot/config/CoreBConfig.java).

We get:

```java
// CoreAConfig.java
...
    @Primary
    @Bean(name = "coreAEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreAEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                                   @Qualifier("coreADS") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("com.example.springboot.core")
                .build();
    }

// CoreBConfig.java
    @Bean(name = "coreBEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreBEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                                   @Qualifier("coreBDS") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("com.example.springboot.core")
                .build();
    }
```

Running `./mvnw test` again will still succeed, but we can notice all models are genrated for each data source:

```
2022-07-22 22:28:18.764  INFO 28411 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2022-07-22 22:28:18.789  INFO 28411 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 5.6.10.Final
2022-07-22 22:28:18.875  INFO 28411 --- [           main] o.hibernate.annotations.common.Version   : HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
2022-07-22 22:28:18.958  INFO 28411 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
Hibernate: 
    
    create table addresses (
       id bigint generated by default as identity,
        address1 varchar(255),
        address2 varchar(255),
        city varchar(255),
        state varchar(255),
        zip_code varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table users (
       id bigint generated by default as identity,
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        middle_name varchar(255),
        phone_number varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table users_addresses (
       id bigint generated by default as identity,
        home_address_flag boolean not null,
        user_id bigint not null,
        address_id bigint,
        primary key (id)
    )
Hibernate: 
    
    alter table users_addresses 
       add constraint FKkiudnofuhukgsdoy6i4ldg43e 
       foreign key (address_id) 
       references addresses
2022-07-22 22:28:19.376  INFO 28411 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2022-07-22 22:28:19.381  INFO 28411 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2022-07-22 22:28:19.414  INFO 28411 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2022-07-22 22:28:19.420  INFO 28411 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-07-22 22:28:19.431  INFO 28411 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2022-07-22 22:28:19.433  INFO 28411 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
Hibernate: 
    
    create table addresses (
       id bigint generated by default as identity,
        address1 varchar(255),
        address2 varchar(255),
        city varchar(255),
        state varchar(255),
        zip_code varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table users (
       id bigint generated by default as identity,
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        middle_name varchar(255),
        phone_number varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table users_addresses (
       id bigint generated by default as identity,
        home_address_flag boolean not null,
        user_id bigint not null,
        address_id bigint,
        primary key (id)
    )
Hibernate: 
    
    alter table users_addresses 
       add constraint FKkiudnofuhukgsdoy6i4ldg43e 
       foreign key (address_id) 
       references addresses
2022-07-22 22:28:19.526  INFO 28411 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2022-07-22 22:28:19.527  INFO 28411 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
```


# Takeaways

Main code and tests demonstrate how to **1)** define multiple data sources and use them in our DAOs, **2)** how to setup tests to handle multiple data sources and initialize those data sources, if need be, for the tests.

1. Different data sources will need to setup a `@Bean` for each data source.
2. If you're in the case where the models are within the same projects (vs. 1 project for each model/data source), make sure to have those in separate folders/packages. Here we have `com.example.springboot.core.coreA` and `com.example.springboot.core.coreB` packages. This will be used when declaring data sources and specifying `packages` (see [CoreAConfig.java](src/main/java/com/example/springboot/config/CoreAConfig.java) and [CoreBConfig.java](src/main/java/com/example/springboot/config/CoreBConfig.java))
3. A `@Primary` data source is needed (or Spring Boot will complain). Just pick one (the one that makes the most sense). See [CoreAConfig.java](src/main/java/com/example/springboot/config/CoreAConfig.java) as example.
4. The [data.sql](https://docs.spring.io/spring-boot/docs/2.6.x/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts) database initialization provided out-of-the-box with Spring/Hibernate no longer applies here. We need to run a similar yet data-source-specific `data.sql` for each data source. To replicate this behavior, 2 `SqlDataSourceScriptDatabaseInitializer` beans are created. See [CoreApplicationTests.java](src/test/java/com/example/springboot/core/CoreApplicationTests.java) for more details.


# References

[1] Spring Boot - DataSourceInitializationConfiguration.java, https://github.com/spring-projects/spring-boot/blob/2.6.x/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/sql/init/DataSourceInitializationConfiguration.java
[2] Spring Boot - SqlDataSourceScriptDatabaseInitializer.java, https://github.com/spring-projects/spring-boot/blob/2.6.x/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/sql/init/SqlDataSourceScriptDatabaseInitializer.java
[3] Spring Boot - DataSourceScriptDatabaseInitializer.java, https://github.com/spring-projects/spring-boot/blob/2.6.x/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jdbc/init/DataSourceScriptDatabaseInitializer.java
[4] Spring Boot - AbstractScriptDatabaseInitializer.java, https://github.com/spring-projects/spring-boot/blob/2.6.x/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/sql/init/AbstractScriptDatabaseInitializer.java