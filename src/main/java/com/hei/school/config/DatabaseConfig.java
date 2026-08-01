package com.hei.school.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

  @Bean
  public DataSource dataSource(
      @Value("${SPRING_DATASOURCE_URL:jdbc:h2:mem:cinema;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}")
          String url,
      @Value("${SPRING_DATASOURCE_USERNAME:sa}") String username,
      @Value("${SPRING_DATASOURCE_PASSWORD:}") String password,
      @Value("${DATABASE_DRIVER:org.h2.Driver}") String driverClassName) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverClassName);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }
}
