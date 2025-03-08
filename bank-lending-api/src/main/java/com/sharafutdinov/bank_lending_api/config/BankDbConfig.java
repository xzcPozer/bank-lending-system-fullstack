package com.sharafutdinov.bank_lending_api.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.sharafutdinov.bank_lending_api.bank_db.repository",
        entityManagerFactoryRef = "bankEntityManagerFactory",
        transactionManagerRef = "bankTransactionManager"
)
public class BankDbConfig {

    @Bean(name = "bankDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.bank")
    public DataSourceProperties bankDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "bankDataSource")
    public DataSource bankDataSource(
            @Qualifier("bankDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "bankEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bankEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bankDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.sharafutdinov.bank_lending_api.bank_db.entity")
                .persistenceUnit("bank")
                .build();
    }

    @Bean(name = "bankTransactionManager")
    public PlatformTransactionManager bankTransactionManager(
            @Qualifier("bankEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}