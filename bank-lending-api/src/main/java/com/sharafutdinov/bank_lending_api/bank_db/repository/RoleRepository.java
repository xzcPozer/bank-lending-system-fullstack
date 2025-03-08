package com.sharafutdinov.bank_lending_api.bank_db.repository;

import com.sharafutdinov.bank_lending_api.bank_db.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
