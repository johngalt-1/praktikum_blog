package ru.yandex.praktikum.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "ru.yandex.praktikum.blog")
@PropertySource("classpath:properties/application.properties")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final String imagesDir;

    public WebConfig(@Value("${images.dir}") String imagesDir) {
        this.imagesDir = imagesDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:///" + imagesDir);
    }
}
