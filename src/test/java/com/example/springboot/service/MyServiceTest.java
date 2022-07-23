package com.example.springboot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.springboot.core.coreA.be.User;
import com.example.springboot.core.coreA.dao.UserRepository;
import com.example.springboot.core.coreB.be.Address;
import com.example.springboot.core.coreB.be.UserAddress;
import com.example.springboot.core.coreB.dao.UserAddressRepository;
import com.example.springboot.service.model.UserAddressList;

@ExtendWith(SpringExtension.class)
public class MyServiceTest {
    
    @InjectMocks
    MyService testee;

    @Mock
    UserRepository userRepositoryMock;

    @Mock
    UserAddressRepository userAddressRepositoryMock;

    @Test
    public void basic() {
        
        User coreUser = User.builder().id(123l).firstName("First").lastName("Last").email("first.last@test.com").build();
        Address coreAddress = Address.builder().address1("1 Sesame Street").city("Dreamland").state("XY").zipCode("12345").build();
        UserAddress coreUserAddress = UserAddress.builder().userId(123l).address(coreAddress).build();

        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(coreUser));
        when(userAddressRepositoryMock.findByUser(anyLong())).thenReturn(Arrays.asList(coreUserAddress));

        UserAddressList actual = testee.getUserAddresses(123l);
        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(coreUser.getId(), actual.getUser().getId());
        assertNotNull(actual.getAddresses());
        assertEquals(1, actual.getAddresses().size());
        assertEquals(coreAddress.getZipCode(), actual.getAddresses().get(0).getZipCode());

    }

}
