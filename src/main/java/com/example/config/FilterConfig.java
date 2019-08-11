package com.example.config;

import com.example.filter.SwaggerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.web.Swagger2Controller;

@Configuration
public class FilterConfig {

    @Bean
    public SwaggerFilter swaggerFilter() {
        return new SwaggerFilter();
    }

    @Bean
    public FilterRegistrationBean<SwaggerFilter> swaggerFilterFilterRegistrationBean(SwaggerFilter swaggerFilter) {
        FilterRegistrationBean<SwaggerFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(swaggerFilter);
        registration.addUrlPatterns(Swagger2Controller.DEFAULT_URL);
        registration.setName("swaggerFilter");
        return registration;
    }

}
