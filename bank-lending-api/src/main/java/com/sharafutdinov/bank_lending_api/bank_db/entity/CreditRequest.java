package com.sharafutdinov.bank_lending_api.bank_db.entity;

import com.sharafutdinov.bank_lending_api.credit_query.WorkerType;
import com.sharafutdinov.bank_lending_api.credit_request.ProcessingStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "credit_request")
@EntityListeners(AuditingEntityListener.class)
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "loan_purpose")
    private String loanPurpose;

    @Column(nullable = false)
    private Integer sum;

    @Column(nullable = false, name = "immovable_property")
    private boolean immovableProperty;

    @Column(nullable = false, name = "movable_property")
    private boolean movableProperty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkerType type;

    @Column(nullable = false, name = "solvency_ref_path")
    private String solvencyRefPath;

    @Column(nullable = true, name = "employment_ref_path")
    private String employmentRefPath;

    @Column(nullable = false, name = "is_processed")
    private boolean isProcessed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    @Column(nullable = true, name = "description_status")
    private String descriptionStatus;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = true, name = "current_loans")
    private Map<String, Object> currentLoans = new HashMap<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false, name = "last_modified_date")
    private LocalDateTime lastModifiedDate;


    @ManyToOne
    @JoinColumn(name = "lending_officer_id")
    private User lendingOfficer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "credit_condition_id", nullable = true)
    private CreditCondition creditCondition;

    @OneToOne(mappedBy = "creditRequest", cascade = CascadeType.ALL)
    private CreditQueryInfo creditQueryInfo;
}
