package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditConditionRepository extends JpaRepository<CreditCondition, Long> {

    CreditCondition findByProductName(String name);
}
