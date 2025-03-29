package com.mainapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
/*
 * Author: Sidharth Guleria
 */
@SpringBootApplication()

public class AuthenticationApp {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApp.class, args);
    }
}
