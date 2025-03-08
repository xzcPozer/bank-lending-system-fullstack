package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    CreditRequest findByUserIdAndIsProcessedIs(Long userId, boolean isProcessed);

    @Query("""
            SELECT cr
            FROM CreditRequest cr
            WHERE cr.user.id = :userId
            """)
    Page<CreditRequest> findAllByUserId(Pageable pageable, Long userId);

    @Query("""
            SELECT cr
            FROM CreditRequest cr
            WHERE cr.isProcessed = :isProcessed
            """)
    Page<CreditRequest> findByIsProcessedIs(Pageable pageable, boolean isProcessed);
}
