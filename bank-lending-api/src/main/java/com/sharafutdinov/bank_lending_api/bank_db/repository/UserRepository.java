package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByPassportSerialNumber(String passportSerialNum);
    User findByEmailOrPhoneNumber(String email, String phoneNumber);
    User findByEmail(String email);
    Page<User> findAll(Pageable pageable);
}
