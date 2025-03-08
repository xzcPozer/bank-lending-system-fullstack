package com.sharafutdinov.bank_lending_api.rospassport_db.repository;

import com.sharafutdinov.bank_lending_api.rospassport_db.entity.Citizens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitizensRepository extends JpaRepository<Citizens, Long> {
    Citizens findByPassportSerialNumber(String serialNumber);
}
