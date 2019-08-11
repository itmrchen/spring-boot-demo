package com.example.service.impl;

import com.example.mapper.MailAttachmentLinkMapper;
import com.example.model.po.MailAttachmentLink;
import com.example.service.IMailAttachmentLinkService;
import org.springframework.stereotype.Service;

@Service
public class MailAttachmentLinkService extends BaseService<MailAttachmentLinkMapper, MailAttachmentLink> implements IMailAttachmentLinkService {
}
