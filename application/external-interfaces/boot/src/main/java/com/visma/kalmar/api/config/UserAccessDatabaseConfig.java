package com.visma.kalmar.api.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.visma.useraccess.kalmar.api",
        entityManagerFactoryRef = "useraccessEntityManagerFactory",
        transactionManagerRef = "useraccessTransactionManager")
public class UserAccessDatabaseConfig {
    @Value("${spring.jpa.database-platform}")
    private String dialect;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties useraccessDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource useraccessDataSource() {
        return useraccessDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "useraccessEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean useraccessEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(useraccessDataSource());
        em.setPackagesToScan("com.visma.useraccess.kalmar.api.**");
        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", dialect);
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager useraccessTransactionManager(
            final @Qualifier("useraccessEntityManagerFactory") LocalContainerEntityManagerFactoryBean
                    ondemandEntityManagerFactory) {

        return new JpaTransactionManager(
                Objects.requireNonNull(ondemandEntityManagerFactory.getObject()));
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        var factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]|"));
        return factory;
    }
}
