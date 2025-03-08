package com.sharafutdinov.bank_lending_api.rospassport_db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "citizens")
public class Citizens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "sur_name", nullable = true)
    private String surName;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @Column(name = "passport_serial_number", unique = true, nullable = false)
    private String passportSerialNumber;
    @Column(name = "address_register", nullable = false)
    private String addressRegister;
    @Column(name = "inn", nullable = false)
    private String INN;
}
