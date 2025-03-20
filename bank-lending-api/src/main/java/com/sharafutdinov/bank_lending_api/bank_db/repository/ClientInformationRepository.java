package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.ClientInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientInformationRepository extends JpaRepository<ClientInformation, Long> {

    ClientInformation findByUserId(Long userId);

    ClientInformation findByUserPassportSerialNumber(String serialNum);
}
