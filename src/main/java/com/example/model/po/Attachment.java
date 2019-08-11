package com.example.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.group.MailInsert;
import com.example.group.MailUpdate;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "附件")
public class Attachment extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "附件id")
    @NotNull(groups = {MailInsert.class, MailUpdate.class}, message = "附件id不能为空")
    private Integer id;
    @ApiModelProperty(value = "名称")
    private String attachmentName;
    @ApiModelProperty(value = "访问地址")
    private String attachmentAddress;
    @ApiModelProperty(value = "本地路径")
    private String attachmentPath;
    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "引用信息")
    private String linkInfo;

}
