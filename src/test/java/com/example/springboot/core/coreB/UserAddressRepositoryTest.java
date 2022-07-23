package com.example.springboot.core.coreB;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.springboot.core.CoreApplicationTests;
import com.example.springboot.core.coreB.be.UserAddress;
import com.example.springboot.core.coreB.dao.UserAddressRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(CoreApplicationTests.class)
public class UserAddressRepositoryTest {
    @Autowired
    private UserAddressRepository testee;

    @Test
    public void load() {
        Optional<UserAddress> a = testee.findById(1L);
        assertNotNull(a);
        assertNotNull(a.get());
    }
}
