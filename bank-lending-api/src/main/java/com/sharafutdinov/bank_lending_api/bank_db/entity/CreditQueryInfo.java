package com.sharafutdinov.bank_lending_api.bank_db.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "credit_query_info")
public class CreditQueryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false, name = "financial_situation")
    private Map<String, Object> financialSituation = new HashMap<>();


    @OneToOne
    @JoinColumn(name = "credit_request_id")
    private CreditRequest creditRequest;
}
