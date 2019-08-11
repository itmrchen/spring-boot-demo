package com.example.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.annotation.IgnoreSwaggerParameter;
import com.example.group.Insert;
import com.example.group.Update;
import com.example.model.BaseModel;
import com.example.model.Tree;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@ApiModel(value = "部门")
public class Dept extends BaseModel implements Tree<Dept> {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "部门id")
    @Null(groups = {Insert.class}, message = "部门id必须为空")
    @NotNull(groups = {Update.class}, message = "部门id不能为空")
    private Integer id;
    @ApiModelProperty(value = "部门名称")
    @NotNull(groups = {Insert.class}, message = "部门名称不能为空")
    private String  deptName;
    @ApiModelProperty(value = "部门等级")
    @Null(groups = {Insert.class, Update.class}, message = "部门等级必须为空")
    private Integer level;
    @ApiModelProperty(value = "上级部门id")
    private Integer pid;
    @ApiModelProperty(value = "顺序")
    @NotNull(groups = {Insert.class}, message = "顺序不能为空")
    private Integer seq;
    @ApiModelProperty(value = "部门全名")
    @Null(groups = {Insert.class, Update.class}, message = "部门全名必须为空")
    private String  fullName;
    @ApiModelProperty(value = "是否新用户默认部门", allowableValues = "1,0")
    @NotNull(groups = {Insert.class, Update.class}, message = "是否新用户默认部门不能为空")
    private Integer isDefault;

    @TableField(exist = false)
    @ApiModelProperty(value = "子部门")
    @IgnoreSwaggerParameter
    private List<Dept> subs;
    @TableField(exist = false)
    @ApiModelProperty(value = "部门下用户")
    @IgnoreSwaggerParameter
    private List<User> users;

}
