package com.example.service.impl;

import com.example.mapper.JobParameterMapper;
import com.example.model.po.JobParameter;
import com.example.service.IJobParameterService;
import org.springframework.stereotype.Service;

@Service
public class JobParameterService extends BaseService<JobParameterMapper, JobParameter> implements IJobParameterService {
}
