package com.example.springboot.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    private long id;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private boolean homeAddress;
}
