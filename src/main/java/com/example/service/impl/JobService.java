package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exception.ProjectException;
import com.example.mapper.JobMapper;
import com.example.model.po.Job;
import com.example.model.po.JobParameter;
import com.example.model.po.JobTemplate;
import com.example.params.Params;
import com.example.service.IJobParameterService;
import com.example.service.IJobService;
import com.example.service.IJobTemplateService;
import io.jsonwebtoken.lang.Collections;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JobService extends BaseService<JobMapper, Job> implements IJobService {

    @Autowired
    private IJobTemplateService jobTemplateService;
    @Autowired
    private IJobParameterService jobParameterService;
    @Autowired
    private Scheduler scheduler;

    @Override
    public IPage<Job> customPage(Page<Job> page, Params<Job> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    public Job customGetById(Integer id) {
        return baseMapper.customSelectById(id);
    }

    @Override
    @Transactional
    public void customSave(Job job) {
        scheduleJob(job);
        baseMapper.insert(job);
        Integer jobId = job.getId();
        List<JobParameter> parameters = job.getParameters();
        if (!Collections.isEmpty(parameters)) {
            for (JobParameter parameter: parameters) {
                parameter.setJobId(jobId);
            }
            jobParameterService.saveBatch(parameters);
        }
    }

    @Override
    @Transactional
    public void customUpdateById(Job job) {
        Integer jobId = job.getId();
        unscheduleJob(jobId);
        scheduleJob(job);
        baseMapper.updateById(job);
        List<JobParameter> parameters = job.getParameters();
        if (Collections.isEmpty(parameters)) {
            parameters = new ArrayList<>();
        } else {
            for (JobParameter parameter: parameters) {
                parameter.setJobId(jobId);
            }
        }
        jobParameterService.merge(parameters, new QueryWrapper<JobParameter>().eq("job_id", jobId));
    }

    @Override
    @Transactional
    public void customRemoveByIds(List<Integer> ids) {
        for (Integer id: ids) {
            unscheduleJob(id);
        }
        super.removeByIds(ids);
    }

    @Override
    @Transactional
    public void pauseTrigger(Integer id) {
        Job job = baseMapper.selectById(id);
        try {
            scheduler.pauseTrigger(new TriggerKey(job.getTriggerName(), job.getTriggerGroup()));
        } catch (SchedulerException e) {
            throw new ProjectException("暂停定时任务触发器时出错", e);
        }
    }

    @Override
    @Transactional
    public void resumeTrigger(Integer id) {
        Job job = baseMapper.selectById(id);
        try {
            scheduler.resumeTrigger(new TriggerKey(job.getTriggerName(), job.getTriggerGroup()));
        } catch (SchedulerException e) {
            throw new ProjectException("恢复定时任务触发器时出错", e);
        }
    }

    // 开启定时任务
    @SuppressWarnings("unchecked")
    private void scheduleJob(Job job) {
        // 任务模板
        JobTemplate jobTemplate = jobTemplateService.customGetById(job.getJobTemplateId());
        // 调度器名称
        String schedName;
        try {
            schedName = scheduler.getSchedulerName();
        } catch (SchedulerException e) {
            throw new ProjectException("获取任务调度器名称出错", e);
        }
        // 名称
        String name = jobTemplate.getJobName() + "-" + UUID.randomUUID().toString();
        // 分组
        String group = jobTemplate.getJobClassName();
        // 任务名称
        String jobName = JOB_PREFIX + name;
        // 任务分组
        String jobGroup = JOB_PREFIX + group;
        // 触发器名称
        String triggerName = TRIGGER_PREFIX + name;
        // 触发器分组
        String triggerGroup = TRIGGER_PREFIX + group;
        // 开始时间
        Long startTime = job.getStartTime();
        // 结束时间
        Long endTime = job.getEndTime();
        // cron
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        // 创建任务
        // 任务类
        Class jobClass;
        try {
            jobClass = Class.forName(jobTemplate.getJobClassName());
        } catch (ClassNotFoundException e) {
            throw new ProjectException("获取任务类对象出错", e);
        }
        JobDetail jobDetail = JobBuilder
                .newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();
        // 设置参数
        List<JobParameter> parameters = job.getParameters();
        if (!Collections.isEmpty(parameters)) {
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            for (JobParameter parameter: parameters) {
                jobDataMap.put(parameter.getParameterName(), parameter.getParameterValue());
            }
        }
        // 创建任务触发器
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(triggerName, triggerGroup).startAt(new Date(startTime));
        if (endTime!=null) {
            triggerBuilder.endAt(new Date(endTime));
        }
        Trigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();
        try {
            // 将触发器与任务绑定到调度器内
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new ProjectException("创建定时任务时出错", e);
        }
        // 设置job属性
        job.setSchedName(schedName);
        job.setJobName(jobName);
        job.setJobGroup(jobGroup);
        job.setTriggerName(triggerName);
        job.setTriggerGroup(triggerGroup);
    }

    // 删除触发器，任务实例会自动删除
    private void unscheduleJob(Integer id) {
        Job job = baseMapper.selectById(id);
        try {
            scheduler.unscheduleJob(new TriggerKey(job.getTriggerName(), job.getTriggerGroup()));
        } catch (SchedulerException e) {
            throw new ProjectException("删除定时任务触发器时出错", e);
        }
    }

}
