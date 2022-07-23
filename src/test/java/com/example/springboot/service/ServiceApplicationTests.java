package com.example.springboot.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.example.springboot.Application;

@Configuration
@ComponentScan(basePackages = "com.example.springboot.service", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Application.class))
public class ServiceApplicationTests {
    
}
