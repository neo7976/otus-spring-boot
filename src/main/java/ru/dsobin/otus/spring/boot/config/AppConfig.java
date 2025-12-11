package ru.dsobin.otus.spring.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("com.mysql.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://localhost:3306/db");
//        ds.setUsername("root");
//        ds.setPassword("root");
//        return ds;
//    }

//    @Bean
//    public void checkPassword() {
//        String raw = "password";
//        String hashed = "$2a$10$PgSKSyUo.189qBD/NViBG.hNyPFKv6/jTjNsS0FDGybmjLhd/DnFy";
//        System.out.println("Matches: " + new BCryptPasswordEncoder().matches(raw, hashed));
//
//        String encode = new BCryptPasswordEncoder().encode("password");
//        System.err.println("password:" + encode);
//    }
}