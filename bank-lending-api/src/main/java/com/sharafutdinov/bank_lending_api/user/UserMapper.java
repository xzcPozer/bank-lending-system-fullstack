package com.sharafutdinov.bank_lending_api.user;

import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.rospassport_db.entity.Citizens;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(Citizens req){
        return User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .surName(req.getSurName())
                .dateOfBirth(req.getDateOfBirth())
                .passportSerialNumber(req.getPassportSerialNumber())
                .addressRegister(req.getAddressRegister())
                .build();
    }

    public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastname(user.getLastName())
                .surName(user.getSurName())
                .dateOfBirth(user.getDateOfBirth())
                .passportSerialNumber(user.getPassportSerialNumber())
                .addressRegister(user.getAddressRegister())
                .addressFact(user.getAddressFact())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
