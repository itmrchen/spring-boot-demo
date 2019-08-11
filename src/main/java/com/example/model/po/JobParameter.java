package com.example.model.po;

import com.example.group.JobInsert;
import com.example.group.JobUpdate;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@ApiModel(value = "定时任务参数")
public class JobParameter extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "参数id")
    @Null(groups = {JobInsert.class}, message = "参数id必须为空")
    private Integer id;
    @ApiModelProperty(value = "参数名称")
    @NotNull(groups = {JobInsert.class, JobUpdate.class}, message = "参数名称不能为空")
    private String parameterName;
    @ApiModelProperty(value = "参数值")
    @NotNull(groups = {JobInsert.class, JobUpdate.class}, message = "参数值不能为空")
    private String parameterValue;
    @ApiModelProperty(value = "定时任务id")
    @Null(groups = {JobInsert.class}, message = "定时任务id必须为空")
    private Integer jobId;

}
