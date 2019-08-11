package com.example.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.annotation.IgnoreSwaggerParameter;
import com.example.group.*;
import com.example.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@ApiModel(value = "用户")
public class User extends BaseModel {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "用户id")
    @NotNull(groups = {UserUpdate.class, MailInsert.class, MailUpdate.class, BroadcastInsert.class}, message = "用户id不能为空")
    @Null(groups = {UserInsert.class, UserInfoInsert.class, UserInfoUpdate.class, BroadcastInsert.class}, message = "用户id必须为空")
    private Integer id;
    @ApiModelProperty(value = "用户名")
    @Null(groups = {UserUpdate.class, UserInfoUpdate.class}, message = "用户名必须为空")
    @NotNull(groups = {UserInsert.class, UserInfoInsert.class, RetrievePasswordMail.class, RetrievePassword.class}, message = "用户名不能为空")
    private String username;
    @ApiModelProperty(value = "密码")
    @NotNull(groups = {UserInsert.class, UserInfoInsert.class, RetrievePassword.class, UserInfoModPwd.class}, message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "邮箱")
    @NotNull(groups = {UserInsert.class, UserUpdate.class, UserInfoInsert.class, UserInfoUpdate.class}, message = "邮箱不能为空")
    @Email(groups = {UserInsert.class, UserUpdate.class, UserInfoInsert.class, UserInfoUpdate.class}, message = "邮箱地址不合法")
    private String email;
    @ApiModelProperty(value = "昵称")
    @NotNull(groups = {UserInsert.class, UserUpdate.class, UserInfoUpdate.class}, message = "昵称不能为空")
    @Null(groups = {UserInfoInsert.class}, message = "昵称必须为空")
    private String nickname;
    @ApiModelProperty(value = "性别", allowableValues = "1,0")
    @NotNull(groups = {UserInsert.class, UserUpdate.class, UserInfoUpdate.class}, message = "性别不能为空")
    @Null(groups = {UserInfoInsert.class}, message = "性别必须为空")
    private Integer sex;
    @ApiModelProperty(value = "生日")
    @NotNull(groups = {UserInsert.class, UserUpdate.class, UserInfoUpdate.class}, message = "生日不能为空")
    @Null(groups = {UserInfoInsert.class}, message = "生日必须为空")
    private Long birth;
    @ApiModelProperty(value="登录时间")
    @Null(groups = {UserInsert.class, UserUpdate.class, UserInfoInsert.class, UserInfoUpdate.class}, message = "登录时间必须为空")
    private Long logintime;
    @ApiModelProperty(value = "部门id")
    @NotNull(groups = {UserInsert.class, UserUpdate.class}, message = "部门id不能为空")
    @Null(groups = {UserInfoInsert.class, UserInfoUpdate.class}, message = "部门id必须为空")
    private Integer deptId;
    @ApiModelProperty(value = "头像id")
    @NotNull(groups = {UserInfoUpdate.class}, message = "头像id不能为空")
    @Null(groups = {UserInfoInsert.class}, message = "头像id必须为空")
    private Integer headImgId;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门信息")
    @IgnoreSwaggerParameter
    private Dept dept;
    @TableField(exist = false)
    @ApiModelProperty(value = "头像")
    @IgnoreSwaggerParameter
    private Attachment headImg;
    @TableField(exist = false)
    @ApiModelProperty(value = "角色信息")
    @IgnoreSwaggerParameter
    @NotEmpty(groups = {UserInsert.class, UserUpdate.class}, message = "角色信息不能为空")
    @Valid
    private List<Role> roles;

}
