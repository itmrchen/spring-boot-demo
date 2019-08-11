package com.example.service;

import com.example.model.po.Job;

public interface IJobService extends IBaseService<Job> {

    // 任务名称和分组前缀
    String JOB_PREFIX = "job-";

    // 触发器名称和分组前缀
    String TRIGGER_PREFIX = "trigger-";

    // 暂停触发器
    void pauseTrigger(Integer id);

    // 取消暂停触发器
    void resumeTrigger(Integer id);

}
