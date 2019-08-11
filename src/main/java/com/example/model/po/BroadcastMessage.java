package com.example.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.annotation.IgnoreSwaggerParameter;
import com.example.group.BroadcastInsert;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@ApiModel(value = "广播消息")
public class BroadcastMessage extends BaseModel {

    private static final long serialVersionUID = 1;
    @ApiModelProperty(value = "消息id")
    @Null(groups = {BroadcastInsert.class}, message = "消息id必须为空")
    private Integer id;
    @ApiModelProperty(value = "发送时间")
    private Long sendTime;
    @ApiModelProperty(value = "消息内容")
    @NotNull(groups = {BroadcastInsert.class}, message = "消息内容不能为空")
    private String content;
    @ApiModelProperty(value = "发送用户id")
    private Integer sendUserId;

    @TableField(exist = false)
    @ApiModelProperty(value = "发送用户")
    @IgnoreSwaggerParameter
    private User sendUser;
    @TableField(exist = false)
    @ApiModelProperty(value = "接收用户")
    @NotNull(groups = {BroadcastInsert.class}, message = "接收用户不能为空")
    @NotEmpty(groups = {BroadcastInsert.class}, message = "接收用户不能为空")
    @IgnoreSwaggerParameter
    private List<User> toUsers;
    @TableField(exist = false)
    @ApiModelProperty(value = "接收用户关联")
    @Null(groups = {BroadcastInsert.class}, message = "接收用户关联必须为空")
    @IgnoreSwaggerParameter
    private List<BroadcastToUserLink> links;

}
