package com.example.service.impl;

import com.example.mapper.MailContentMapper;
import com.example.model.po.MailContent;
import com.example.service.IMailContentService;
import org.springframework.stereotype.Service;

@Service
public class MailContentService extends BaseService<MailContentMapper, MailContent> implements IMailContentService {
}
