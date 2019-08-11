package com.example.service.impl;

import com.example.mapper.MailToUserLinkMapper;
import com.example.model.po.MailToUserLink;
import com.example.service.IMailToUserLinkService;
import org.springframework.stereotype.Service;

@Service
public class MailToUserLinkService extends BaseService<MailToUserLinkMapper, MailToUserLink> implements IMailToUserLinkService {
}
