package com.pekochu.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.telegram.telegrambots.ApiContextInitializer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableScheduling
public class Application {

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class.getCanonicalName());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApiContextInitializer.init();
        LOGGER.info("PekoServices Public Server initiated");
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Lista
        List<String> domains = new ArrayList<>();
        domains.add("https://pekochu.com");
        domains.add("https://blog.pekochu.com");
        domains.add("https://cloud.pekochu.com");
        domains.add("https://server.pekochu.com");
        domains.add("https://angelbrv.com");
        domains.add("https://blog.angelbrv.com");
        // Configurar CORS
        config.setAllowedOrigins(domains);
        config.setMaxAge(3600L);
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
