package com.example.model.po;

import com.example.group.MailInsert;
import com.example.group.MailUpdate;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@ApiModel("邮件内容")
public class MailContent extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("邮件内容id")
    @Null(groups = {MailInsert.class}, message = "邮件内容id必须为空")
    @NotNull(groups = {MailUpdate.class}, message = "邮件内容id不能为空")
    private Integer id;
    @ApiModelProperty("邮件内容")
    @NotNull(groups = {MailInsert.class, MailUpdate.class}, message = "邮件内容不能为空")
    private String content;
    @ApiModelProperty("邮件id")
    @Null(groups = {MailInsert.class}, message = "邮件id必须为空")
    private Integer mailId;

}
