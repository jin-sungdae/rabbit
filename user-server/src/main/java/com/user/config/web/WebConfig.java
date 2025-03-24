package com.user.config.web;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Objects;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final Environment env;

    /**
     * MethodInterceptor 적용
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (Objects.equals(env.getProperty("spring.profiles.active"), "local")) {
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:///" + env.getProperty("user.upload.path") + "/");
        }
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }



    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 모든 API 허용
                        .allowedOrigins("http://localhost:3000") // 프론트엔드 허용
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }



    /**
     * JSON View
     */
    @Bean
    public MappingJackson2JsonView jsonView() {
        return new MappingJackson2JsonView();
    }


}
