package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.group.JobInsert;
import com.example.group.JobUpdate;
import com.example.model.po.Job;
import com.example.model.vo.JobVO;
import com.example.model.vo.ResultVO;
import com.example.params.Params;
import com.example.service.IJobService;
import com.example.util.ModelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.example.model.vo.ResultVO.SUCCESS;

@RestController
@RequestMapping(value = "/job", produces = "application/json; charset=UTF-8")
@Api(tags = "定时任务")
@Validated
public class JobController {

    @Autowired
    private IJobService jobService;

    @GetMapping("/{current}/{size}")
    @ApiOperation("查询定时任务列表")
    @SuppressWarnings("unchecked")
    public ResultVO findPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                         @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                         JobVO jobVO) {
        Page<Job> page = new Page<>(current, size);
        Params<Job> params = new Params<>(jobVO);
        IPage<Job> iPage = jobService.customPage(page, params);
        IPage jobs = (IPage) ModelUtil.copy(iPage, new ModelUtil.Mapping(Job.class, JobVO.class));
        if (jobs!=null) {
            jobs.getRecords().forEach(o -> {
                if (StringUtils.isEmpty(((JobVO) o).getTriggerState())) {
                    ((JobVO) o).setTriggerState("EXPIRE");
                }
            });
        }
        return new ResultVO<>(SUCCESS, "", jobs);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询定时任务")
    public ResultVO<JobVO> findById(@PathVariable @NotNull(message = "任务id不能为空") @ApiParam(value = "任务id", required = true) Integer id) {
        Job job = jobService.customGetById(id);
        JobVO jobVO = (JobVO) ModelUtil.copy(job, new ModelUtil.Mapping(Job.class, JobVO.class));
        if (jobVO!=null) {
            if (StringUtils.isEmpty(jobVO.getTriggerState())) {
                jobVO.setTriggerState("EXPIRE");
            }
        }
        return new ResultVO<>(SUCCESS, "", jobVO);
    }

    @PostMapping
    @ApiOperation("保存定时任务")
    public ResultVO save(@Validated({JobInsert.class}) @RequestBody JobVO jobVO) {
        Job job = (Job) ModelUtil.copy(jobVO, new ModelUtil.Mapping(JobVO.class, Job.class));
        jobService.customSave(job);
        return new ResultVO<>(SUCCESS, "保存定时任务成功！", null);
    }

    @PutMapping
    @ApiOperation("更新定时任务")
    public ResultVO update(@Validated({JobUpdate.class}) @RequestBody JobVO jobVO) {
        Job job = (Job) ModelUtil.copy(jobVO, new ModelUtil.Mapping(JobVO.class, Job.class));
        jobService.customUpdateById(job);
        return new ResultVO<>(SUCCESS, "更新定时任务成功！", null);
    }

    @DeleteMapping("/{ids}")
    @ApiOperation("删除定时任务")
    public ResultVO delete(
            @PathVariable
            @NotNull(message = "任务id不能为空")
            @NotEmpty(message = "任务id不能为空")
            @ApiParam(value = "任务id，多个用逗号分隔", required = true) List<Integer> ids) {
        jobService.customRemoveByIds(ids);
        return new ResultVO<>(SUCCESS, "删除定时任务成功！", null);
    }

    @PatchMapping("/pause/{id}")
    @ApiOperation("暂停定时任务")
    public ResultVO pause(@PathVariable @NotNull(message = "任务id不能为空") @ApiParam(value = "任务id", required = true) Integer id) {
        jobService.pauseTrigger(id);
        return new ResultVO<>(SUCCESS, "暂停定时任务成功！", null);
    }

    @PatchMapping("/resume/{id}")
    @ApiOperation("恢复定时任务")
    public ResultVO resume(@PathVariable @NotNull(message = "任务id不能为空") @ApiParam(value = "任务id", required = true) Integer id) {
        jobService.resumeTrigger(id);
        return new ResultVO<>(SUCCESS, "恢复定时任务成功！", null);
    }

}
