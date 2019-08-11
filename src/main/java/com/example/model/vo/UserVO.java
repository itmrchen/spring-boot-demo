package com.example.model.vo;

import com.example.group.UserInfoModPwd;
import com.example.model.po.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserVO extends User {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "新密码")
    @NotNull(groups = {UserInfoModPwd.class}, message = "新密码不能为空")
    private String newpassword;

}
