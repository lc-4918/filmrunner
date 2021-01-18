package com.keziko.dvdtek.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Nom de classe : CorsConfig
 * @version 04/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200","https://filmrunner.herokuapp.com")
                    .allowedMethods("GET","POST","PUT","PATCH","DELETE")
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization")
                    .allowCredentials(true);
        }
}
