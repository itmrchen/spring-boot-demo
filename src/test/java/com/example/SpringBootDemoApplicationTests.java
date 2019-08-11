package com.example;

import com.example.model.po.User;
import com.example.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class SpringBootDemoApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private IUserService userService;

    @Test
    public void testPassword() {
        System.out.println(passwordEncoder.encode("123"));
    }

    @Test
    public void testContext() {
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = context.getBean(name);
            System.out.println(bean.getClass());
        }
    }

    @Test
    public void testClone() {
        User user = userService.customGetById(9);
        System.out.println(user);
        User user_ = (User) user.clone();
        System.out.println(user_);
    }

}
