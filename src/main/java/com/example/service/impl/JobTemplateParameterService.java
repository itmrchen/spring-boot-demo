package com.example.service.impl;

import com.example.mapper.JobTemplateParameterMapper;
import com.example.model.po.JobTemplateParameter;
import com.example.service.IJobTemplateParameterService;
import org.springframework.stereotype.Service;

@Service
public class JobTemplateParameterService extends BaseService<JobTemplateParameterMapper, JobTemplateParameter> implements IJobTemplateParameterService {
}
