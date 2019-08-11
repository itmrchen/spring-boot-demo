package com.example.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.annotation.IgnoreSwaggerParameter;
import com.example.group.JobTemplateInsert;
import com.example.group.JobTemplateUpdate;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@ApiModel(value = "任务模板")
public class JobTemplate extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "任务id")
    @Null(groups = {JobTemplateInsert.class}, message = "任务id必须为空")
    @NotNull(groups = {JobTemplateUpdate.class}, message = "任务id不能为空")
    private Integer id;
    @ApiModelProperty(value = "任务名称")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "任务名称不能为空")
    private String jobName;
    @ApiModelProperty(value = "任务类名")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "任务类名不能为空")
    private String jobClassName; // org.quartz.Job的实现类或org.springframework.scheduling.quartz.QuartzJobBean的子类
    @ApiModelProperty(value = "任务描述")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "任务描述不能为空")
    private String jobDesc;
    @ApiModelProperty(value = "创建时间")
    @Null(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "创建时间必须为空")
    private Long createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务参数")
    @IgnoreSwaggerParameter
    @Valid
    private List<JobTemplateParameter> parameters;

}
