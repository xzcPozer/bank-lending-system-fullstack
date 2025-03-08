package com.sharafutdinov.bank_lending_api.bank_db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "client_information")
public class ClientInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Double balance;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = true, name = "previous_loans")
    private List<Map<String, Object>> previousLoans = new ArrayList<>();

    @Column(nullable = true, name = "credit_score")
    private Integer creditScore;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = true)
    private User user;
}
