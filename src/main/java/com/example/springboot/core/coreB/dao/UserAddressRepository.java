package com.example.springboot.core.coreB.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.springboot.core.coreB.be.UserAddress;

public interface UserAddressRepository extends CrudRepository<UserAddress, Long> {
    
    @Query(value = "select ua from UserAddress ua where userId = :userId ")
    List<UserAddress> findByUser(@Param("userId") long userId);

}
