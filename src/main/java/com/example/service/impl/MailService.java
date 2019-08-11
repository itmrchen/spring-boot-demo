package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exception.LogicException;
import com.example.ftp.SftpHelper;
import com.example.mapper.MailMapper;
import com.example.model.po.*;
import com.example.params.Params;
import com.example.service.IMailAttachmentLinkService;
import com.example.service.IMailContentService;
import com.example.service.IMailService;
import com.example.service.IMailToUserLinkService;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MailService extends BaseService<MailMapper, Mail> implements IMailService {

    @Value("${mail.max-retry}")
    private Integer maxRetry;
    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${attachment.file-separator}")
    private String fileSeparator;
    @Autowired
    private IMailContentService mailContentService;
    @Autowired
    private IMailToUserLinkService mailToUserLinkService;
    @Autowired
    private IMailAttachmentLinkService mailAttachmentLinkService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SftpHelper sftpHelper;

    @Override
    public IPage<Mail> customPage(Page<Mail> page, Params<Mail> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    public Mail customGetById(Integer id) {
        return baseMapper.customSelectById(id);
    }

    @Override
    @Transactional
    public void customSave(Mail mail) {
        // 保存邮件
        long now = System.currentTimeMillis();
        mail.setCreateTime(now);
        mail.setMailStatus(DRAFT_STATUS);
        baseMapper.insert(mail);
        Integer mailId = mail.getId();
        // 保存内容
        MailContent content = mail.getMailContent();
        content.setMailId(mailId);
        mailContentService.save(content);
        // 保存收件人
        List<User> toUsers = mail.getToUsers();
        if (!Collections.isEmpty(toUsers)) {
            List<MailToUserLink> links = new ArrayList<>();
            for (User toUser : toUsers) {
                links.add(new MailToUserLink(mailId, toUser.getId()));
            }
            mailToUserLinkService.saveBatch(links);
        }
        // 保存附件
        List<Attachment> attachments = mail.getAttachments();
        if (!Collections.isEmpty(attachments)) {
            List<MailAttachmentLink> links = new ArrayList<>();
            for (Attachment attachment : attachments) {
                links.add(new MailAttachmentLink(mailId, attachment.getId()));
            }
            mailAttachmentLinkService.saveBatch(links);
        }
    }

    @Override
    @Transactional
    public void customUpdateById(Mail mail) {
        Integer mailId = mail.getId();
        Mail oldMail = baseMapper.selectById(mailId);
        if (SENT_STATUS.equals(oldMail.getMailStatus())) {
            throw new LogicException("已发送的邮件不能修改！");
        }
        // 更新邮件
        baseMapper.updateById(mail);
        // 更新内容
        MailContent content = mail.getMailContent();
        mailContentService.updateById(content);
        // 更新收件人
        List<User> toUsers = mail.getToUsers();
        List<MailToUserLink> toUserLinks = new ArrayList<>();
        if (!Collections.isEmpty(toUsers)) {
            for (User toUser : toUsers) {
                toUserLinks.add(new MailToUserLink(mailId, toUser.getId()));
            }
            mailToUserLinkService.merge(toUserLinks, new QueryWrapper<MailToUserLink>().eq("mail_id", mailId));
        }
        // 更新附件
        List<Attachment> attachments = mail.getAttachments();
        List<MailAttachmentLink> attachmentLinks = new ArrayList<>();
        if (!Collections.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                attachmentLinks.add(new MailAttachmentLink(mailId, attachment.getId()));
            }
        }
        mailAttachmentLinkService.merge(attachmentLinks, new QueryWrapper<MailAttachmentLink>().eq("mail_id", mailId));
    }

    @Override
    @Transactional
    public Mail send(Integer id) {
        Mail mail = baseMapper.customSelectById(id);
        String status = mail.getMailStatus();
        if (!"draft".equals(status) && !"fail".equals(status)) {
            throw new LogicException("不能重复发送邮件");
        }
        User currentUser = currentUser();
        if (currentUser!=null) {
            mail.setSendUserId(currentUser.getId());
        }
        long now = System.currentTimeMillis();
        mail.setSendTime(now);
        mail.setMailStatus(SENDING_STATUS);
        baseMapper.updateById(mail);
        return mail;
    }

    @Override
    @Transactional
    @Async
    @CacheEvict(cacheNames = {"summary:mail:month", "summary:mail:user"}, allEntries = true)
    public void send(Mail mail, Integer maxRetry) {
        for (int i=0; i<maxRetry; i++) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
                // 发件人
                messageHelper.setFrom(mailUsername);
                // 收件人
                List<User> toUsers = mail.getToUsers();
                List<String> toMails = new ArrayList<>();
                if (!Collections.isEmpty(toUsers)) {
                    for (User toUser : toUsers) {
                        toMails.add(toUser.getEmail());
                    }
                }
                messageHelper.setTo(toMails.toArray(new String[0]));
                // 标题
                messageHelper.setSubject(mail.getMailSubject());
                // 内容
                messageHelper.setText(mail.getMailContent().getContent(), true);
                // 附件
                List<Attachment> attachments = mail.getAttachments();
                if (!Collections.isEmpty(attachments)) {
                    for (Attachment attachment : attachments) {
                        String path = attachment.getAttachmentPath();
                        String dir = path.substring(0, path.lastIndexOf(fileSeparator));
                        String name = path.substring(path.lastIndexOf(fileSeparator)+1);
                        ByteArrayResource byteArrayResource = new ByteArrayResource(sftpHelper.download(dir, name));
                        messageHelper.addAttachment(attachment.getAttachmentName(), byteArrayResource);
                    }
                }
                javaMailSender.send(message);
                mail.setMailStatus(SENT_STATUS);
                baseMapper.updateById(mail);
                return;
            } catch (MessagingException e) {
                if (i==maxRetry-1) {
                    mail.setMailStatus(FAIL_STATUS);
                    baseMapper.updateById(mail);
                    log.error("发送邮件["+mail.getId()+"]失败");
                } else {
                    log.error("发送邮件["+mail.getId()+"]失败，准备重试");
                }
            }
        }
    }

    @Override
    @Transactional
    @Async
    public void send(Mail mail) {
        send(mail, maxRetry);
    }
}
