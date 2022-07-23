package com.example.springboot.core.coreB.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.core.coreB.be.Address;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {
    
}
