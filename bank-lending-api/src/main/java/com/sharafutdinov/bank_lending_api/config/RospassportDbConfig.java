package com.sharafutdinov.bank_lending_api.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
        basePackages = "com.sharafutdinov.bank_lending_api.rospassport_db.repository",
        entityManagerFactoryRef = "rospassportEntityManagerFactory",
        transactionManagerRef = "rospassportTransactionManager"
)
public class RospassportDbConfig {

    @Bean(name = "rospassportDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.rospassport")
    public DataSourceProperties bankDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "rospassportDataSource")
    public DataSource secondaryDataSource(
            @Qualifier("rospassportDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "rospassportEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("rospassportDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.sharafutdinov.bank_lending_api.rospassport_db.entity")
                .persistenceUnit("rospassport")
                .build();
    }

    @Bean(name = "rospassportTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("rospassportEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
