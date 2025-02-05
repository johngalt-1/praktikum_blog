package ru.yandex.praktikum.blog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "ru.yandex.praktikum.blog")
@PropertySource("classpath:properties/application.properties")
@EnableWebMvc
public class WebConfig {}
