package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exception.LogicException;
import com.example.mapper.UserMapper;
import com.example.model.po.*;
import com.example.params.Params;
import com.example.service.IMailService;
import com.example.service.IUserRoleLinkService;
import com.example.service.IUserService;
import com.google.common.collect.Lists;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService extends BaseService<UserMapper, User> implements IUserService {

    @Value("${retrieve-password.min-wait}")
    private Integer minWait;
    @Value("${retrieve-password.expire}")
    private Integer expire;
    @Autowired
    private IUserRoleLinkService userRoleLinkService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<User> customList(Params<User> params) {
        return baseMapper.customSelectList(params);
    }

    @Override
    public IPage<User> customPage(Page<User> page, Params<User> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    public User customGetById(Integer id) {
        return baseMapper.customSelectById(id);
    }

    @Override
    public User customGetOne(Params<User> params) {
        return baseMapper.customSelectOne(params);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"summary:user:sex"}, allEntries = true)
    public void customSave(User user) {
        checkUsernameAndEmail(user);
        baseMapper.insert(user);
        Integer userId = user.getId();
        List<Role> roles = user.getRoles();
        List<UserRoleLink> links = new ArrayList<>();
        if (!Collections.isEmpty(roles)) {
            for (Role role: roles) {
                links.add(new UserRoleLink(userId, role.getId()));
            }
            userRoleLinkService.saveBatch(links);
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"summary:user:sex"}, allEntries = true)
    public void customUpdateById(User user) {
        checkUsernameAndEmail(user);
        baseMapper.updateById(user);
        Integer userId = user.getId();
        List<Role> roles = user.getRoles();
        List<UserRoleLink> links = new ArrayList<>();
        if (!Collections.isEmpty(roles)) {
            for (Role role: roles) {
                links.add(new UserRoleLink(userId, role.getId()));
            }
            userRoleLinkService.merge(links, new QueryWrapper<UserRoleLink>().eq("user_id", userId));
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"summary:user:sex"}, allEntries = true)
    public boolean updateById(User user) {
        checkUsernameAndEmail(user);
        return super.updateById(user);
    }

    private void checkUsernameAndEmail(User user) {
        String username = user.getUsername();
        if (StringUtils.isNotEmpty(username)) {
            User byUsername = baseMapper.selectOne(new QueryWrapper<>(new User().setUsername(username)));
            if (byUsername!=null && !byUsername.getId().equals(user.getId())) {
                throw new LogicException("用户名已存在！");
            }
        }
        String email = user.getEmail();
        if (StringUtils.isNotEmpty(email)) {
            User byEmail = baseMapper.selectOne(new QueryWrapper<>(new User().setEmail(email)));
            if (byEmail!=null && !byEmail.getId().equals(user.getId())) {
                throw new LogicException("邮箱已存在！");
            }
        }
    }

    @Override
    @Transactional
    public Mail retrievePasswordMail(User user) {
        User user_ = baseMapper.selectOne(new QueryWrapper<>(new User().setUsername(user.getUsername())));
        if (user_==null) {
            throw new LogicException("用户不存在，请检查用户名是否正确");
        }
        // 查询redis中是否已存在验证码
        String key = RETRIEVE_PASSWORD_VERIFICATION_PREFIX+user.getUsername();
        Verification verification = (Verification) redisTemplate.opsForValue().get(key);
        long now = System.currentTimeMillis();
        if (verification!=null) {
            // 检查是否到时间
            long nextCanSendTime = verification.getNextCanSendTime();
            if (nextCanSendTime > now) {
                throw new LogicException("发送过快，请稍后再试！");
            }
        }
        String code = RandomStringUtils.randomAlphanumeric(6);
        long nextCanSendTime = now + minWait*1000;
        Verification verification_ = new Verification();
        verification_.setCode(code);
        verification_.setNextCanSendTime(nextCanSendTime);
        verification_.setStatus(true);
        Mail mail = createRetrievePasswordMail(user_, code);
        mailService.customSave(mail);
        // 将验证码保存到redis
        redisTemplate.boundValueOps(key).set(verification_, expire*60, TimeUnit.SECONDS);
        return mail;
    }

    // 找回密码邮件
    private Mail createRetrievePasswordMail(User user, String code) {
        Mail mail = new Mail();
        mail.setMailSubject("REACT中后台找回密码");
        mail.setMailType("info");
        String content = "";
        content += "<p>您好：</p>";
        content += "<p>您在REACT中后台申请找回密码，验证码：<span style='color:#F00'>"+code+"</span>，有效期"+expire+"分钟。</p>";
        content += "<p>如果您没有进行过找回密码的操作，请无视此邮件。</p>";
        content += "<p>----------------------------------</p>";
        content += "<p>REACT中后台</p>";
        content += "<p>"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"</p>";
        mail.setMailContent(new MailContent().setContent(content));
        mail.setToUsers(Lists.newArrayList(user));
        return mail;
    }

    @Override
    @Transactional
    public void retrievePassword(User user, String code) {
        String key = RETRIEVE_PASSWORD_VERIFICATION_PREFIX+user.getUsername();
        Verification verification = (Verification) redisTemplate.opsForValue().get(key);
        if (verification==null || !code.equals(verification.getCode()) || !verification.isStatus()) {
            throw new LogicException("验证码错误！");
        }
        User user_ = baseMapper.selectOne(new QueryWrapper<>(new User().setUsername(user.getUsername())));
        user_.setPassword(user.getPassword());
        baseMapper.updateById(user_);
        // 修改验证码状态
        verification.setStatus(false);
        redisTemplate.boundValueOps(key).set(verification, minWait, TimeUnit.SECONDS);
    }

}
