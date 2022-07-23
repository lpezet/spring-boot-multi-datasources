package com.example.springboot.core.coreA.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.core.coreA.be.User;

@Repository
public interface  UserRepository extends CrudRepository<User, Long> {
    
}
