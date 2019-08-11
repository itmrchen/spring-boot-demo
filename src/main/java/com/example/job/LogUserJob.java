package com.example.job;

import com.example.mapper.UserMapper;
import com.example.model.po.User;
import com.example.params.Params;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class LogUserJob extends QuartzJobBean {

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("----------logUserJob开始了----------");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String username = jobDataMap.getString("username");
        log.info("查询username="+username+"的用户信息，并打印");
        Params<User> params = new Params<>(new User().setUsername(username));
        User user = userMapper.customSelectOne(params);
        log.info(user.toString());
        log.info("----------logUserJob结束了----------");
    }

}
