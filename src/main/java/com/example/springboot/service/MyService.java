package com.example.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.core.coreA.dao.UserRepository;
import com.example.springboot.core.coreB.be.UserAddress;
import com.example.springboot.core.coreB.dao.UserAddressRepository;
import com.example.springboot.service.model.Address;
import com.example.springboot.service.model.User;
import com.example.springboot.service.model.UserAddressList;

@Service
public class MyService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAddressRepository userAddressRepository;

    public UserAddressList getUserAddresses(long userId) {
        Optional<com.example.springboot.core.coreA.be.User> maybeUser = userRepository.findById(userId);
        com.example.springboot.core.coreA.be.User coreUser = maybeUser.get();
        List<UserAddress> coreAddresses = userAddressRepository.findByUser(userId);
        
        User user = User.builder().id(coreUser.getId()).name(coreUser.getFirstName() + " " + coreUser.getLastName()).build();

        List<Address> addresses = new ArrayList<>();
        coreAddresses.forEach(ua -> {
            com.example.springboot.core.coreB.be.Address coreAddress = ua.getAddress();
            addresses.add(Address.builder()
                .address1(coreAddress.getAddress1())
                .address2(coreAddress.getAddress2())
                .city(coreAddress.getCity())
                .state(coreAddress.getState())
                .zipCode(coreAddress.getZipCode())
                .homeAddress( ua.isHomeAddressFlag() )
            .build());
        });

        return UserAddressList.builder().user(user).addresses(addresses).build();
    }

}
