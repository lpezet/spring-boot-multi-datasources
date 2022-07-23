package com.example.springboot.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * Note the use of @Primary in this configuration class.
 * Otherwise you get:
 * - org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "coreAEntityManagerFactory",
        transactionManagerRef = "coreATransactionManager",
        basePackages = { "com.example.springboot.core.coreA" }
)
public class CoreAConfig {

    
    @Primary
    @Bean(name="coreADS")
    @ConfigurationProperties(prefix="spring.datasource.corea")
    public DataSource coreADataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "coreAEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreAEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                                   @Qualifier("coreADS") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("com.example.springboot.core.coreA")
                .build();
    }

    @Primary
    @Bean(name = "coreATransactionManager")
    public PlatformTransactionManager coreATransactionManager(
            @Qualifier("coreAEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
