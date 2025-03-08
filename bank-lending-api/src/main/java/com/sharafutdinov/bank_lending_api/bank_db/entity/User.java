package com.sharafutdinov.bank_lending_api.bank_db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "_user")
public class User {

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
    @Column(name = "address_fact", nullable = false)
    private String addressFact;
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = true)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // Имя промежуточной таблицы
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ClientInformation clientInformation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CreditRequest> creditRequest;

    @OneToMany(mappedBy = "lendingOfficer", cascade = CascadeType.ALL)
    private List<CreditRequest> creditLendingOfficerRequest;

}
