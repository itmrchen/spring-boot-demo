package com.example.model.po;

import com.example.group.JobTemplateInsert;
import com.example.group.JobTemplateUpdate;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@ApiModel(value = "任务模板参数")
public class JobTemplateParameter extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "参数id")
    @Null(groups = {JobTemplateInsert.class}, message = "参数id必须为空")
    private Integer id;
    @ApiModelProperty(value = "参数名称")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "参数名称不能为空")
    private String parameterName;
    @ApiModelProperty(value = "参数类型")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "参数类型不能为空")
    private String parameterType;
    @ApiModelProperty(value = "参数描述")
    @NotNull(groups = {JobTemplateInsert.class, JobTemplateUpdate.class}, message = "参数描述不能为空")
    private String parameterDesc;
    @ApiModelProperty(value = "任务模板id")
    @Null(groups = {JobTemplateInsert.class}, message = "任务模板id必须为空")
    private Integer jobTemplateId;

}
