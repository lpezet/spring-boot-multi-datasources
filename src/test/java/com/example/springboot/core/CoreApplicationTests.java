package com.example.springboot.core;



import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.example.springboot.Application;

@Configuration
@ComponentScan(basePackages = "com.example.springboot", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Application.class))
public class CoreApplicationTests {
    @Bean
    public SqlDataSourceScriptDatabaseInitializer initCoreADb(@Qualifier("coreADS") DataSource ds) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
		settings.setDataLocations(Arrays.asList("coreA_data.sql"));
        return new SqlDataSourceScriptDatabaseInitializer(ds, settings);
    }

    @Bean
    public SqlDataSourceScriptDatabaseInitializer initCoreBDb(@Qualifier("coreBDS") DataSource ds) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
		settings.setDataLocations(Arrays.asList("coreB_data.sql"));
        return new SqlDataSourceScriptDatabaseInitializer(ds, settings);
    }

}
