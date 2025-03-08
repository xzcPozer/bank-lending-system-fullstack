package com.sharafutdinov.bank_lending_api.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean(name = "bankLiquibaseProperties")
    public LiquibaseProperties bankLiquibaseProperties(
            @Qualifier("bankDataSourceProperties") DataSourceProperties properties
    ) {
        LiquibaseProperties liquibaseProperties = new LiquibaseProperties();
        liquibaseProperties.setUrl(properties.getUrl());
        liquibaseProperties.setUser(properties.getUsername());
        liquibaseProperties.setPassword(properties.getPassword());
        liquibaseProperties.setChangeLog("classpath:db/changelog/db.changelog-bank.yaml");
        return liquibaseProperties;
    }

    @Bean(name = "rospassportLiquibaseProperties")
    public LiquibaseProperties rospassportLiquibaseProperties(
            @Qualifier("rospassportDataSourceProperties") DataSourceProperties properties
    ) {
        LiquibaseProperties liquibaseProperties = new LiquibaseProperties();
        liquibaseProperties.setUrl(properties.getUrl());
        liquibaseProperties.setUser(properties.getUsername());
        liquibaseProperties.setPassword(properties.getPassword());
        liquibaseProperties.setChangeLog("classpath:db/changelog/db.changelog-rospassport.yaml");
        return liquibaseProperties;
    }

    @Bean(name = "bankLiquibase")
    public SpringLiquibase bankLiquibase(
            @Qualifier("bankDataSource") DataSource dataSource,
            @Qualifier("bankLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        return liquibase;
    }

    @Bean(name = "rospassportLiquibase")
    public SpringLiquibase rospassportLiquibase(
            @Qualifier("rospassportDataSource") DataSource dataSource,
            @Qualifier("rospassportLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        return liquibase;
    }
}