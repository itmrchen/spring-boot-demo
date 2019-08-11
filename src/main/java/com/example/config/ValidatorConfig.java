package com.example.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

// 参数校验
@Configuration
public class ValidatorConfig {

    // hibernate validator
    @Bean
    public Validator validator() {
        return Validation
                .byProvider(HibernateValidator.class)
                .configure()
                // 快速返回模式，有一个验证失败立即返回错误信息
//                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }

}
