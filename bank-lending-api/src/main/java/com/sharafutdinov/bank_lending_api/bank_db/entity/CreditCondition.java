package com.sharafutdinov.bank_lending_api.bank_db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "credit_condition")
public class CreditCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "min_amount")
    private Integer minAmount;

    @Column(nullable = false, name = "max_amount")
    private Integer maxAmount;

    @Column(nullable = false, name = "interest_rate")
    private Double interestRate;

    @Column(nullable = false, name = "term_min")
    private Integer termMin;

    @Column(nullable = false, name = "term_max")
    private Integer termMax;

    @Column(nullable = false, name = "product_name")
    private String productName;


    @OneToMany(mappedBy = "creditCondition", cascade = CascadeType.ALL)
    private List<CreditRequest> creditRequests;
}
