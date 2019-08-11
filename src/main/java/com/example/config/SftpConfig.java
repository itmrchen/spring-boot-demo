package com.example.config;

import com.example.ftp.SftpFactory;
import com.example.ftp.SftpHelper;
import com.example.ftp.SftpPool;
import com.example.ftp.SftpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ftp配置
@Configuration
@EnableConfigurationProperties(SftpProperties.class)
public class SftpConfig {

    // 工厂
    @Bean
    public SftpFactory sftpFactory(SftpProperties properties) {
        return new SftpFactory(properties);
    }

    // 连接池
    @Bean
    public SftpPool sftpPool(SftpFactory sftpFactory) {
        return new SftpPool(sftpFactory);
    }

    // 辅助类
    @Bean
    public SftpHelper sftpHelper(SftpPool sftpPool) {
        return new SftpHelper(sftpPool);
    }

}
