package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditQueriesRepository extends JpaRepository<CreditQueryInfo, Long> {
//    @Query(value = "SELECT financial_situation ->> :key credit_queries WHERE id = :id", nativeQuery = true)
//    String findFinancialSituationByKey(String key, Long id);

    CreditQueryInfo findByCreditRequestId(Long creditRequestId);

    Page<CreditQueryInfo> findAllByCreditRequestUserLastName(Pageable pageable, String lastname);
}
