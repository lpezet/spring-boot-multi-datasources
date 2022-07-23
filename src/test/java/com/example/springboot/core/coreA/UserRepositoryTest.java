package com.example.springboot.core.coreA;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.springboot.core.CoreApplicationTests;
import com.example.springboot.core.coreA.be.User;
import com.example.springboot.core.coreA.dao.UserRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(CoreApplicationTests.class)
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository testee;

    @Test
    public void load() {
        Optional<User> a = testee.findById(1L);
        assertNotNull(a);
        assertNotNull(a.get());
    }

}
